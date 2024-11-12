const movieController = require("../controllers/movieData.controller");
const authenticate = require("../middlewares/authorization.middlewares");
const commentController = require("../controllers/commentUser.controller");
const { sendJsonResponse } = require('../utils/http.helpers');

async function movieRouter(req, res) {
  const urlParts = req.url.split('/').filter(Boolean); // Divide la URL en partes
  const method = req.method;

  // Rutas de películas
  if (method === 'GET' && req.url === '/moviesAll') {
    return await movieController.findAll(req, res);

  } else if (method === 'POST' && req.url === '/add') {
    return await movieController.movieData(req, res);

  } else if (method === 'DELETE' && urlParts[0] === 'delete' && urlParts[1]) {
    req.params = { identifier: urlParts[1] };
    return await movieController.deleteById(req, res);

  // Rutas autenticadas para obtener películas
  } else if (method === 'GET' && req.url === '/mostViewed') {
    if (!(await authenticate(req, res))) return;
    return await movieController.getMostViewedMovies(req, res);

  } else if (method === 'GET' && req.url === '/recentMovies') {
    if (!(await authenticate(req, res))) return;
    return await movieController.getMostRecentMovies(req, res);

  } else if (method === 'GET' && urlParts[0] === 'search' && urlParts[1]) {
    if (!(await authenticate(req, res))) return; // Autenticación
    req.params = { title: decodeURIComponent(urlParts[1]) }; // Decodificar el título
    return await movieController.searchMovieByTitle(req, res);

  } else if (method === 'GET' && urlParts[0] === 'moviesId' && urlParts[1]) {
    if (!(await authenticate(req, res))) return;
    req.params = { id: urlParts[1] };
    return await movieController.getMovieById(req, res);

  // Rutas para comentarios
  } else if (method === 'POST' && urlParts[0] === 'moviesId' && urlParts[1] && urlParts[2] === 'postComment') {
    if (!(await authenticate(req, res))) return;
    req.params = { id: urlParts[1] };
    return await commentController.postComment(req, res);

  } else if (method === 'GET' && urlParts[0] === 'moviesId' && urlParts[1] && urlParts[2] === 'comments') {
    if (!(await authenticate(req, res))) return;
    req.params = { id: urlParts[1], parentId: urlParts[3] || null };
    return await commentController.getComments(req, res);

  } else if (method === 'GET' && urlParts[0] === 'moviesId' && urlParts[1] && urlParts[2] === 'pollComments') {
    if (!(await authenticate(req, res))) return;
    req.params = { id: urlParts[1] };
    return await commentController.pollComments(req, res);

  // Rutas de calificación
  } else if (method === 'POST' && urlParts[0] === 'moviesId' && urlParts[1] && urlParts[2] === 'rate') {
    if (!(await authenticate(req, res))) return;
    req.params = { id: urlParts[1] };
    return await movieController.rateMovie(req, res);

  } else if (method === 'GET' && urlParts[0] === 'moviesId' && urlParts[2] === 'average-rating') {
    if (!(await authenticate(req, res))) return;
    req.params = { movieId: urlParts[1] };
    return await movieController.getMovieAverageRating(req, res);

  } else if (method === 'GET' && req.url === '/topRatedMovies') {
    if (!(await authenticate(req, res))) return;
    return await movieController.getTopRatedMoviesOverall(req, res);

  } else if (method === 'POST' && urlParts[0] === 'moviesId' && urlParts[2] === 'wishlist' && urlParts[3] === 'add') {
    if (!(await authenticate(req, res))) return;
    req.params = { id: urlParts[1] };
    return await movieController.addToWishlist(req, res);

  } else if (method === 'GET' && req.url === '/wishlist') {
    if (!(await authenticate(req, res))) return;
    return await movieController.getWishlist(req, res);

  } else if (method === 'GET' && req.url === '/ratedMovies') {
    if (!(await authenticate(req, res))) return;
    return await movieController.getRatedMovies(req, res);

  } else if (method === 'GET' && urlParts[0] === 'moviesId' && urlParts[2] === 'userRatings') {
    if (!(await authenticate(req, res))) return;
    req.params = { movieId: urlParts[1] };
    return await movieController.getUserRatingsForMovie(req, res);

  // Rutas de administración para eliminar comentarios
  } else if (method === 'DELETE' && urlParts[0] === 'comments' && urlParts[1]) {
    if (!(await authenticate(req, res))) return;
    req.params = { id: urlParts[1] };
    return await commentController.deleteComment(req, res);

  } else if (method === 'DELETE' && urlParts[0] === 'replies' && urlParts[1]) {
    if (!(await authenticate(req, res))) return;
    req.params = { id: urlParts[1] };
    return await commentController.deleteReply(req, res);
  }

  // Ruta no encontrada
  sendJsonResponse(res, 404, { error: 'Ruta no encontrada' });
}

module.exports = movieRouter;
