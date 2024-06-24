const controller = {};
const commentUser = require("../models/commentUser.model");
const httpError = require("http-errors");
const User = require('../models/account.model');
const Notification=require('../models/notification.model')




controller.postComment = async (req, res, next) => {
  try {
      const userId = req.user._id;
      const { id: movieId } = req.params; // Usar el parámetro de ruta
      const { commentText, parentId } = req.body;

      const user = await User.findById(userId);
      if (!user) {
          throw httpError(404, 'Usuario no encontrado');
      }

      if (parentId) {
          const parentComment = await commentUser.findById(parentId);
          if (!parentComment) {
              throw httpError(404, 'Comentario padre no encontrado');
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
              userId: parentComment.userId,
              message: `@${user.username} ha respondido tu comentario`
          });
          await notification.save();

          res.status(201).json({ message: 'Respuesta agregada exitosamente' });
      } else {
          const newComment = new commentUser({
              movieId,
              userId,
              commentText
          });

          await newComment.save();
          res.status(201).json({ message: 'Comentario agregado exitosamente' });
      }
  } catch (error) {
      next(error);
  }
};

// Función para obtener comentarios y respuestas
controller.getComments = async (req, res, next) => {
  try {
  
    const { id: movieId } = req.params;
    const { parentId } = req.query;
    let commentsQuery = { movieId, parentId: null };

    if (parentId) {
      commentsQuery = { movieId, parentId };
    }

    const comments = await commentUser.find(commentsQuery).populate('userId', 'username avatar').sort({ createdAt: -1 });
    res.status(200).json(comments);
  } catch (error) {
    next(error);
  }
};


// Función para obtener respuestas a un comentario específico
controller.getRepliesToComment = async (req, res, next) => {
    try {
     
      const { id: movieId, parentId } = req.params;
  
      const replies = await commentUser.find({ movieId, parentId }).populate('userId', 'username avatar').sort({ createdAt: 1 });

      res.status(200).json(replies);
    } catch (error) {
      next(error);
    }
  };

  controller.getNotifications = async (req, res, next) => {
    try {
      const userId = req.user._id;
      const notifications = await Notification.find({ userId }).populate('userId', 'username avatar').sort({ createdAt: -1 }).limit(10); ;
      res.status(200).json(notifications);
    } catch (error) {
      next(error);
    }
  };
  
  controller.markAsRead = async (req, res, next) => {
    try {
      const { id } = req.params;
      const notification = await Notification.findById(id);
      if (!notification) {
        throw httpError(404, 'Notificación no encontrada');
      }
      notification.read = true;
      await notification.save();
      res.status(200).json({ message: 'Notificación marcada como leída' });
    } catch (error) {
      next(error);
    }
  };
  

  //borrar comentario padre
  // Función para eliminar un comentario y sus respuestas
controller.deleteComment = async (req, res, next) => {
  try {
      const { id: commentId } = req.params;

      const comment = await commentUser.findById(commentId);
      if (!comment) {
          throw httpError(404, 'Comentario no encontrado');
      }

      // Eliminar todas las respuestas del comentario
      await commentUser.deleteMany({ parentId: commentId });

      // Eliminar el comentario
      await commentUser.findByIdAndDelete(commentId);

      res.status(200).json({ message: 'Comentario y sus respuestas eliminados exitosamente' });
  } catch (error) {
      next(error);
  }
};


//eliminar comentarios hijos
// Función para eliminar una respuesta específica
controller.deleteReply = async (req, res, next) => {
  try {
      const { id: replyId } = req.params;

      const reply = await commentUser.findById(replyId);
      if (!reply) {
          throw httpError(404, 'Respuesta no encontrada');
      }

      // Eliminar la respuesta
      await commentUser.findByIdAndDelete(replyId);

      res.status(200).json({ message: 'Respuesta eliminada exitosamente' });
  } catch (error) {
      next(error);
  }
};
  
module.exports = controller;
