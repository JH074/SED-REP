const controller = {};
const commentUser = require("../models/commentUser.model");
const User = require("../models/account.model");
const Notification = require("../models/notification.model");
const moment = require("moment-timezone");
const { sendJsonResponse, parseRequestBody } = require("../utils/http.helpers");

// Agrega un comentario o respuesta
controller.postComment = async (req, res) => {
  try {
    // Extraemos los datos necesarios del cuerpo y la URL
    const { commentText, parentId } = await parseRequestBody(req); // Parseamos el cuerpo de la solicitud
    const movieId = req.params.id; // Obtenemos el `id` de la película desde req.params
    const userId = req.user._id; // Obtenemos el userId autenticado

    // Verificamos si el usuario existe
    const user = await User.findById(userId);
    if (!user) {
      return sendJsonResponse(res, 404, { error: 'Usuario no encontrado' });
    }

    // Si `parentId` existe, estamos creando una respuesta a otro comentario
    if (parentId) {
      const parentComment = await commentUser.findById(parentId);
      if (!parentComment) {
        return sendJsonResponse(res, 404, { error: 'Comentario padre no encontrado' });
      }

      const newReply = new commentUser({
        movieId,
        userId,
        commentText,
        parentId
      });

      await newReply.save();

      // Enviar notificación al autor del comentario padre
      const notification = new Notification({
        userId: parentComment.userId, // El destinatario de la notificación es el autor del comentario padre
        message: `@${user.username} ha respondido tu comentario`,
        avatar: user.avatar, // Solo agregar el avatar del usuario que respondió
        parentId: parentComment._id // Agregar el parentId del comentario padre
      });
      await notification.save();

      // Respuesta de éxito
      sendJsonResponse(res, 201, { message: 'Respuesta agregada exitosamente' });
    } else {
      // Crear un nuevo comentario si no existe `parentId`
      const newComment = new commentUser({
        movieId,
        userId,
        commentText
      });

      await newComment.save();
      sendJsonResponse(res, 201, { message: 'Comentario agregado exitosamente' });
    }
  } catch (error) {
    // Manejo de errores
    sendJsonResponse(res, 500, { error: error.message });
  }
};

// Obtener comentarios y respuestas
controller.getComments = async (req, res) => {
  try {
    const movieId = req.params.id;
    const parentId = req.params.parentId || null; // Usa `req.params.parentId`
    
    const commentsQuery = { movieId, parentId }; // Construye el objeto de consulta

    const comments = await commentUser
      .find(commentsQuery)
      .populate("userId", "username avatar")
      .sort({ createdAt: -1 });

    const formattedComments = comments.map(comment => ({
      ...comment.toObject(),
      createdAt: moment(comment.createdAt).tz("America/El_Salvador").format("YYYY-MM-DD HH:mm:ss"),
      updatedAt: moment(comment.updatedAt).tz("America/El_Salvador").format("YYYY-MM-DD HH:mm:ss"),
    }));

    sendJsonResponse(res, 200, formattedComments);
  } catch (error) {
    sendJsonResponse(res, 500, { error: error.message });
  }
};



// Obtener nuevos comentarios
controller.pollComments = async (req, res) => {
  try {
    // Extraemos el parámetro `id` de la película y la consulta `lastFetched` de la URL
    const movieId = req.params.id;
    const url = new URL(req.url, `http://${req.headers.host}`);
    const lastFetched = url.searchParams.get("lastFetched");

    // Obtenemos los nuevos comentarios creados después de `lastFetched`
    const newComments = await commentUser.find({
      movieId,
      createdAt: { $gt: lastFetched }
    }).populate('userId', 'username avatar').sort({ createdAt: 1 });

    // Formateamos las fechas de los comentarios
    const formattedComments = newComments.map(comment => ({
      ...comment.toObject(),
      createdAt: moment(comment.createdAt).tz('America/El_Salvador').format('YYYY-MM-DD HH:mm:ss'),
      updatedAt: moment(comment.updatedAt).tz('America/El_Salvador').format('YYYY-MM-DD HH:mm:ss')
    }));

    // Enviamos la respuesta con los comentarios formateados
    sendJsonResponse(res, 200, formattedComments);
  } catch (error) {
    sendJsonResponse(res, 500, { error: error.message });
  }
};

// Obtener respuestas a un comentario específico
controller.getRepliesToComment = async (req, res) => {
  try {
    // Obtenemos los parámetros `id` y `parentId` de la URL
    const movieId = req.params.id;
    const parentId = req.params.parentId;

    // Buscamos las respuestas al comentario específico
    const replies = await commentUser.find({ movieId, parentId })
      .populate('userId', 'username avatar')
      .sort({ createdAt: 1 });

    // Formateamos las fechas de las respuestas a la zona horaria de El Salvador
    const formattedReplies = replies.map(reply => ({
      ...reply.toObject(),
      createdAt: moment(reply.createdAt).tz('America/El_Salvador').format('YYYY-MM-DD HH:mm:ss'),
      updatedAt: moment(reply.updatedAt).tz('America/El_Salvador').format('YYYY-MM-DD HH:mm:ss')
    }));

    // Enviamos la respuesta con las respuestas formateadas
    sendJsonResponse(res, 200, formattedReplies);
  } catch (error) {
    sendJsonResponse(res, 500, { error: error.message });
  }
};

// Obtener notificaciones de usuario
controller.getNotifications = async (req, res) => {
  try {
    const userId = req.user._id;
    const notifications = await Notification.find({ userId }).populate("userId", "username avatar").sort({ createdAt: -1 }).limit(10);

    sendJsonResponse(res, 200, notifications);
  } catch (error) {
    sendJsonResponse(res, 500, { error: error.message });
  }
};

// Marcar notificación como leída
controller.markAsRead = async (req, res) => {
  try {
    const notificationId = req.params.id;
    const notification = await Notification.findById(notificationId);
    if (!notification) return sendJsonResponse(res, 404, { error: "Notificación no encontrada" });

    notification.read = true;
    await notification.save();
    sendJsonResponse(res, 200, { message: "Notificación marcada como leída" });
  } catch (error) {
    sendJsonResponse(res, 500, { error: error.message });
  }
};

// Eliminar un comentario y sus respuestas
controller.deleteComment = async (req, res) => {
  try {
    // Obtenemos el `id` del comentario desde `req.params`
    const commentId = req.params.id;

    // Buscamos el comentario en la base de datos
    const comment = await commentUser.findById(commentId);
    if (!comment) {
      return sendJsonResponse(res, 404, { error: 'Comentario no encontrado' });
    }

    // Eliminamos todas las respuestas al comentario
    await commentUser.deleteMany({ parentId: commentId });

    // Eliminamos el comentario principal
    await commentUser.findByIdAndDelete(commentId);

    // Enviamos una respuesta de éxito
    sendJsonResponse(res, 200, { message: 'Comentario y sus respuestas eliminados exitosamente' });
  } catch (error) {
    sendJsonResponse(res, 500, { error: error.message });
  }
};

// Eliminar una respuesta específica
controller.deleteReply = async (req, res) => {
  try {
    // Obtenemos el `id` de la respuesta desde `req.params`
    const replyId = req.params.id;

    // Buscamos la respuesta en la base de datos
    const reply = await commentUser.findById(replyId);
    if (!reply) {
      return sendJsonResponse(res, 404, { error: 'Respuesta no encontrada' });
    }

    // Eliminamos la respuesta
    await commentUser.findByIdAndDelete(replyId);

    // Enviamos una respuesta de éxito
    sendJsonResponse(res, 200, { message: 'Respuesta eliminada exitosamente' });
  } catch (error) {
    sendJsonResponse(res, 500, { error: error.message });
  }
};

module.exports = controller;
