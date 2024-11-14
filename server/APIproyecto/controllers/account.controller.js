const controller = {};
const User = require("../models/account.model");
const { createToken, verifyToken } = require("../utils/jwl.tools");
const { sendJsonResponse, parseRequestBody } = require("../utils/http.helpers");

controller.register = async (req, res) => {
  try {
    // Parseamos el cuerpo de la solicitud para obtener los datos
    const body = await parseRequestBody(req);

    const {
      username,
      email,
      password,
      year_nac,
      genere,
      movie_genere,
      avatar,
      role
    } = body;

    // Verificamos si el usuario ya existe
    const user = await User.findOne({ $or: [{ email: email }] });
    if (user) {
      return sendJsonResponse(res, 409, { error: "Ya existe esta cuenta" });
    }

    // Creamos un nuevo usuario
    const newUser = new User({
      username: username,
      email: email,
      password: password,
      year_nac: year_nac,
      genere: genere,
      movie_genere: movie_genere,
      avatar: avatar,
      role: role // Establecemos el rol proporcionado
    });

    await newUser.save();

    // Enviamos la respuesta de éxito
    sendJsonResponse(res, 201, { message: "Se ha creado correctamente tu usuario" });
  } catch (error) {
    // Manejo de errores
    sendJsonResponse(res, 500, { error: error.message });
  }
};

controller.login = async (req, res) => {
  try {
    // Parseamos el cuerpo de la solicitud para obtener los datos de login
    const { email, password } = await parseRequestBody(req);

    // Buscamos al usuario por email
    const user = await User.findOne({ email });

    if (!user) {
      return sendJsonResponse(res, 404, { error: "El usuario no se ha encontrado" });
    }

    // Verificamos la contraseña
    if (!user.comparePassword(password)) {
      return sendJsonResponse(res, 401, { error: "Contraseña incorrecta" });
    }

    // Generamos el token de sesión
    const token = await createToken(user._id);

    // Manejamos los tokens para mantener las últimas 5 sesiones
    let _tokens = [...user.tokens];
    const _verifyPromise = _tokens.map(async (_t) => {
      const status = await verifyToken(_t);
      return status ? _t : null;
    });

    _tokens = (await Promise.all(_verifyPromise)).filter(Boolean).slice(0, 4);
    _tokens = [token, ..._tokens];
    user.tokens = _tokens;

    // Guardamos los cambios del usuario
    await user.save();

    // Determinamos la ruta de redirección según el rol del usuario
    const redirectPath = user.role === 'admin' ? '/user/admin/home' : '/user/home';

    // Enviamos la respuesta de éxito con el token y la ruta de redirección
    sendJsonResponse(res, 200, {
      message: 'Se ha iniciado sesión correctamente',
      token,
      role: user.role,
      redirectPath // Añadimos el path de redirección
    });
  } catch (error) {
    // Manejo de errores
    sendJsonResponse(res, 500, { error: error.message });
  }
};

controller.logout = async (req, res) => {
  try {
    const token = req.headers.authorization.split(" ")[1];
    const payload = await verifyToken(token);

    if (!payload) {
      return sendJsonResponse(res, 401, { error: "Token inválido o expirado" });
    }

    const userId = payload.sub;
    const user = await User.findById(userId);

    if (!user) {
      return sendJsonResponse(res, 404, { error: "Usuario no encontrado" });
    }

    // Eliminar el token de la lista de tokens del usuario
    user.tokens = user.tokens.filter(t => t !== token);
    await user.save();

    return sendJsonResponse(res, 200, {
      message: 'Se ha cerrado sesión correctamente'
    });
  } catch (error) {
    return sendJsonResponse(res, 500, { error: error.message });
  }
};

module.exports = controller;