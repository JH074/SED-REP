const movieController = require("../controllers/movieData.controller");
const authenticate = require("../middlewares/authorization.middlewares");
const commentController = require("../controllers/commentUser.controller");
const { sendJsonResponse } = require('../utils/http.helpers');

async function movieRouter(req, res) {
  const urlParts = req.url.split('/').filter(Boolean); // Divide la URL en partes
  const method = req.method;
  console.log(`MovieRouter received request - Method: ${method}, URL: ${req.url}`); // Log de entrada a movieRouter

  try {
    // Ruta para obtener promedio de calificaciones de una película
    if (method === 'GET' && urlParts[0] === 'moviesId' && urlParts[1] && urlParts[2] === 'rating-average') {
      if (!(await authenticate(req, res))) return;
      req.params = { movieId: urlParts[1] };
      console.log(`Fetching average rating for movieId: ${req.params.movieId}`);
      return await movieController.getAverageRatingForMovie(req, res);

    // Ruta para obtener todos los comentarios de una película
    } else if (method === 'GET' && urlParts[0] === 'moviesId' && urlParts[1] && urlParts[2] === 'comments') {
      if (!(await authenticate(req, res))) return;
      
      req.params = { id: urlParts[1], parentId: urlParts[3] || null }; // Configura `parentId` en `req.params`
      console.log(`Fetching comments for movieId: ${req.params.id}`);
      
      return await commentController.getComments(req, res);
    
    
    

    // Ruta para obtener detalles de una película específica
    } else if (method === 'GET' && urlParts[0] === 'moviesId' && urlParts.length === 2) {
      if (!(await authenticate(req, res))) return;
      req.params = { id: urlParts[1] };
      console.log(`Requesting movie details for movieId: ${req.params.id}`);
      return await movieController.getMovieById(req, res);

    // Ruta para buscar películas por título
    } else if (method === 'GET' && urlParts[0] === 'search' && urlParts.length === 2) {
      if (!(await authenticate(req, res))) return;
      req.params = { title: decodeURIComponent(urlParts[1]) };
      console.log(`Searching movies with title: ${req.params.title}`);
      return await movieController.searchMovieByTitle(req, res);

    // Ruta para obtener películas más vistas
    } else if (method === 'GET' && req.url === '/mostViewed') {
      if (!(await authenticate(req, res))) return;
      console.log("Fetching most viewed movies");
      return await movieController.getMostViewedMovies(req, res);

    // Ruta para obtener películas más recientes
    } else if (method === 'GET' && req.url === '/recentMovies') {
      if (!(await authenticate(req, res))) return;
      console.log("Fetching most recent movies");
      return await movieController.getMostRecentMovies(req, res);

    // Ruta para agregar una película
    } else if (method === 'POST' && req.url === '/add') {
      console.log("Adding a new movie");
      return await movieController.movieData(req, res);

    // Ruta para eliminar una película específica
    } else if (method === 'DELETE' && urlParts[0] === 'delete' && urlParts.length === 2) {
      req.params = { identifier: urlParts[1] };
      console.log(`Deleting movie with identifier: ${req.params.identifier}`);
      return await movieController.deleteById(req, res);

    // Ruta para obtener todas las películas
    } else if (method === 'GET' && req.url === '/moviesAll') {
      console.log("Fetching all movies");
      return await movieController.findAll(req, res);

    // Ruta para agregar comentario a una película
    } else if (method === 'POST' && urlParts[0] === 'moviesId' && urlParts[1] && urlParts[2] === 'postComment') {
      if (!(await authenticate(req, res))) return;
      req.params = { id: urlParts[1] };
      console.log(`Posting comment for movieId: ${req.params.id}`);
      return await commentController.postComment(req, res);

    // Ruta para agregar película a la lista de deseos del usuario
    } else if (method === 'POST' && urlParts[0] === 'moviesId' && urlParts[2] === 'wishlist' && urlParts[3] === 'add') {
      if (!(await authenticate(req, res))) return;
      req.params = { id: urlParts[1] };
      return await movieController.addToWishlist(req, res);

    // Ruta para obtener la lista de deseos del usuario
    } else if (method === 'GET' && req.url === '/wishlist') {
      if (!(await authenticate(req, res))) return;
      console.log("Fetching wishlist");
      return await movieController.getWishlist(req, res);

    // Ruta para obtener películas calificadas por el usuario
    } else if (method === 'GET' && req.url === '/ratedMovies') {
      if (!(await authenticate(req, res))) return;
      console.log("Fetching rated movies");
      return await movieController.getRatedMovies(req, res);

    // Ruta para obtener calificaciones de usuario para una película específica
    } else if (method === 'GET' && urlParts[0] === 'moviesId' && urlParts[2] === 'userRatings') {
      if (!(await authenticate(req, res))) return;
      req.params = { movieId: urlParts[1] };
      return await movieController.getUserRatingsForMovie(req, res);

    // Ruta para calificar una película
    } else if (method === 'POST' && urlParts[0] === 'moviesId' && urlParts[1] && urlParts[2] === 'rate') {
      if (!(await authenticate(req, res))) return;
      req.params = { id: urlParts[1] };
      return await movieController.rateMovie(req, res);

    // Ruta no encontrada
    } else {
      sendJsonResponse(res, 404, { error: 'Ruta no encontrada' });
    }
  } catch (error) {
    console.error(`Error in movieRouter: ${error.message}`);
    sendJsonResponse(res, 500, { error: 'Internal Server Error' });
  }
}

module.exports = movieRouter;
