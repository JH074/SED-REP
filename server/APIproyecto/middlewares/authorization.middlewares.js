const { verifyToken } = require('../utils/jwl.tools');
const User = require('../models/account.model');
const { sendJsonResponse } = require('../utils/http.helpers');

const authenticate = async (req, res) => {
  try {
    const authHeader = req.headers.authorization;
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return sendJsonResponse(res, 401, { error: 'No autorizado' });
    }

    const token = authHeader.split(' ')[1];
    const payload = await verifyToken(token);
    if (!payload) {
      return sendJsonResponse(res, 401, { error: 'Token inválido' });
    }

    const user = await User.findById(payload.sub);
    if (!user) {
      return sendJsonResponse(res, 401, { error: 'Usuario no encontrado' });
    }

    // Si quieres habilitar la verificación de roles, puedes descomentar esto:
    // if (user.role !== 'admin') {
    //     return sendJsonResponse(res, 403, { error: 'Acceso denegado. Permiso de administrador requerido.' });
    // }

    req.user = user;
    return true; // Para indicar que la autenticación fue exitosa
  } catch (error) {
    sendJsonResponse(res, 500, { error: error.message });
  }
};

module.exports = authenticate;
