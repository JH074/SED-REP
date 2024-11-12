const AccountController = require("../controllers/account.controller");
const movieController = require("../controllers/movieData.controller");
const userLoginController = require("../controllers/userLogin.controller");
const authenticate = require("../middlewares/authorization.middlewares");
const notificationController = require("../controllers/commentUser.controller");
const { sendJsonResponse } = require('../utils/http.helpers');

async function userRouter(req, res) {
  const urlParts = req.url.split('/').filter(Boolean);
  const method = req.method;

  // Ruta para registro de usuario
  if (method === 'POST' && req.url === '/register') {
    return await AccountController.register(req, res);

  // Ruta para iniciar sesión
  } else if (method === 'POST' && req.url === '/login') {
    return await AccountController.login(req, res);

  // Ruta para cerrar sesión (requiere autenticación)
  } else if (method === 'POST' && req.url === '/logout') {
    if (!(await authenticate(req, res))) return;
    return await AccountController.logout(req, res);

  // Obtener datos del usuario autenticado en la ruta `/user/home`
  } else if (method === 'GET' && req.url === '/user/home') {
    if (!(await authenticate(req, res))) return;
    return await userLoginController.getUserData(req, res);

  // Obtener notificaciones del usuario autenticado
  } else if (method === 'GET' && req.url === '/user/notifications') {
    if (!(await authenticate(req, res))) return;
    return await notificationController.getNotifications(req, res);

  // Marcar notificación como leída usando `:id`
  } else if (method === 'PATCH' && urlParts[0] === 'user' && urlParts[1] === 'notifications' && urlParts[2]) {
    if (!(await authenticate(req, res))) return;
    req.params = { id: urlParts[2] };
    return await notificationController.markAsRead(req, res);

  // Crear una nueva película (ruta de administrador)
  } else if (method === 'POST' && req.url === '/user/admin/home/movies') {
    return await movieController.movieData(req, res);

  // Eliminar una película por ID en la ruta `/user/admin/home/movies/:id`
  } else if (method === 'DELETE' && urlParts[0] === 'user' && urlParts[2] === 'home' && urlParts[3] === 'movies' && urlParts[4]) {
    req.params = { id: urlParts[4] };
    return await movieController.deleteMovie(req, res);

  // Obtener todas las películas creadas por ID en la ruta `/user/admin/home/:id`
  } else if (method === 'GET' && urlParts[0] === 'user' && urlParts[2] === 'home' && urlParts[3]) {
    if (!(await authenticate(req, res))) return;
    req.params = { id: urlParts[3] };
    return await movieController.getMovieByAdminId(req, res);

  // Buscar actores por nombre en la ruta `/user/admin/home/movies/actors/search/:actorName`
  } else if (method === 'GET' && urlParts[0] === 'user' && urlParts[2] === 'home' && urlParts[4] === 'actors' && urlParts[5] === 'search' && urlParts[6]) {
    if (!(await authenticate(req, res))) return;
    req.params = { actorName: urlParts[6] };
    return await movieController.searchActorsByName(req, res);

  // Obtener todas las películas creadas en la ruta `/user/admin/home`
  } else if (method === 'GET' && req.url === '/user/admin/home') {
    return await movieController.getAllMovies(req, res);
  }

  // Ruta no encontrada
  sendJsonResponse(res, 404, { error: 'Ruta no encontrada' });
}

module.exports = userRouter;
