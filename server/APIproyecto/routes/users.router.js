const AccountController = require("../controllers/account.controller");
const movieController = require("../controllers/movieData.controller");
const userLoginController = require("../controllers/userLogin.controller");
const authenticate = require("../middlewares/authorization.middlewares");
const notificationController = require("../controllers/commentUser.controller");
const { sendJsonResponse } = require('../utils/http.helpers');

// Función para verificar si el usuario es administrador
async function isAuthenticatedAndAdmin(req, res) {
  if (!(await authenticate(req, res))) return false;
  return req.user && req.user.role === 'admin';
}

async function userRouter(req, res) {
  const urlParts = req.url.split('/').filter(Boolean);
  const method = req.method;

  // Log para ver las rutas y métodos
  console.log(`userRouter - Method: ${method}, URL: ${req.url}`);

  // Rutas de usuario estándar
  if (method === 'POST' && req.url === '/register') {
    return await AccountController.register(req, res);

  } else if (method === 'POST' && req.url === '/login') {
    return await AccountController.login(req, res);

  } else if (method === 'POST' && req.url === '/logout') {
    if (!(await authenticate(req, res))) return;
    return await AccountController.logout(req, res);

  // Ruta específica de usuario para datos de usuario en /user/home
  } else if (method === 'GET' && req.url === '/user/home') {
    if (!(await authenticate(req, res))) return;
    if (req.user.role === 'admin') {
      // Si el usuario es administrador, redirigir a la página de administrador
      return await movieController.getAllMovies(req, res);
    } else {
      // Si el usuario es estándar, redirigir a la página de usuario
      return await userLoginController.getUserData(req, res);
    }

  // Ruta de notificaciones de usuario
  } else if (method === 'GET' && req.url === '/user/notifications') {
    if (!(await authenticate(req, res))) return;
    return await notificationController.getNotifications(req, res);

  // Ruta para marcar notificación como leída
  } else if (method === 'PATCH' && urlParts[0] === 'user' && urlParts[1] === 'notifications' && urlParts[2]) {
    if (!(await authenticate(req, res))) return;
    req.params = { id: urlParts[2] };
    return await notificationController.markAsRead(req, res);

  // Rutas de administrador
  } else if (method === 'POST' && req.url === '/user/admin/home/movies') {
    if (!(await isAuthenticatedAndAdmin(req, res))) return;
    return await movieController.movieData(req, res);

  } else if (method === 'DELETE' && urlParts[0] === 'user' && urlParts[2] === 'home' && urlParts[3] === 'movies' && urlParts[4]) {
    if (!(await isAuthenticatedAndAdmin(req, res))) return;
    req.params = { id: urlParts[4] };
    return await movieController.deleteMovie(req, res);

  } else if (method === 'GET' && urlParts[0] === 'user' && urlParts[2] === 'home' && urlParts[3]) {
    if (!(await isAuthenticatedAndAdmin(req, res))) return;
    req.params = { id: urlParts[3] };
    return await movieController.getMovieByAdminId(req, res);

  } else if (method === 'GET' && urlParts[0] === 'user' && urlParts[2] === 'home' && urlParts[4] === 'actors' && urlParts[5] === 'search' && urlParts[6]) {
    if (!(await isAuthenticatedAndAdmin(req, res))) return;
    req.params = { actorName: urlParts[6] };
    return await movieController.searchActorsByName(req, res);

  } else if (method === 'GET' && req.url === '/user/admin/home') {
    if (!(await isAuthenticatedAndAdmin(req, res))) return;
    return await movieController.getAllMovies(req, res);
  }

  // Ruta no encontrada
  sendJsonResponse(res, 404, { error: 'Ruta no encontrada' });
}

module.exports = userRouter;
