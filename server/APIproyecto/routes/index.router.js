const userRouter = require('./users.router');
const movieRouter = require('./movie.router');
const { sendJsonResponse } = require('../utils/http.helpers');

function apiRouter(req, res) {
  console.log(`Incoming request URL: ${req.url}`); // Log inicial para ver la URL de entrada

  if (req.url.startsWith('/account')) {
    req.url = req.url.replace('/account', '');
    console.log(`Redirected to userRouter with modified URL: ${req.url}`);
    return userRouter(req, res);
  } else if (req.url.startsWith('/movies')) {
    req.url = req.url.replace('/movies', '');
    console.log(`Redirected to movieRouter with modified URL: ${req.url}`);
    return movieRouter(req, res);
  } else {
    sendJsonResponse(res, 404, { error: 'Ruta no encontrada' });
  }
}

module.exports = apiRouter;
