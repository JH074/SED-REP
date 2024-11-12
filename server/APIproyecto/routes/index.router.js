const userRouter = require('./users.router');
const movieRouter = require('./movie.router');
const { sendJsonResponse } = require('../utils/http.helpers');

function apiRouter(req, res) {
  if (req.url.startsWith('/account')) {
    req.url = req.url.replace('/account', ''); // Eliminar el prefijo para que userRouter lo maneje directamente
    return userRouter(req, res);
  } else if (req.url.startsWith('/movies')) {
    // Eliminar el prefijo '/movies' de req.url para que `movieRouter` lo procese directamente
    req.url = req.url.replace('/movies', '');
    return movieRouter(req, res);
  } else {
    sendJsonResponse(res, 404, { error: 'Ruta no encontrada' });
  }
}

module.exports = apiRouter;
