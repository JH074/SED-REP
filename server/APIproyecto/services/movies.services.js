
const axios = require("axios");

const BASE_URL_API="https://api.themoviedb.org/3/"

const API_KEY="376635a2a15525fb5b00a5b6ac0f2861"

const User = require('../models/account.model');

const genreMap = {
    "accion": 28,
    "aventura": 12,
    "animacion": 16,
    "comedia": 35,
    "crimen": 80,
    "documental": 99,
    "drama": 18,
    "familia": 10751,
    "fantasia": 14,
    "historia": 36,
    "terror": 27,
    "musica": 10402,
    "misterio": 9648,
    "romance": 10749,
    "suspenso": 53,
    "belica": 10752,
    "western": 37
    // Agrega más géneros según necesites
  };

const getMovies=async()=>{

    try {
        const response = await axios.get(`${BASE_URL_API}discover/movie?api_key=${API_KEY}&language=es-MX`);
        return response.data;

    } catch (error) {
     
        throw new Error("Error occurred while creating the form. Please try again.");
    }
}


const hideMovie = async (movieId) => {
  try {
    const movie = await Movie.findOne({ id: movieId });
    if (!movie) {
      throw new Error('Película no encontrada');
    }

    movie.isHidden = true;
    await movie.save();

    return { message: 'Película ocultada exitosamente' };
  } catch (error) {
    throw new Error(error.message);
  }
};



//buscar PELICULAS
// Buscar películas
const normalizeString = (str) => {
  return str.normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase();
};

const searchMovieByTitle = async (title, userId, sortBy = 'relevancia', genre = null) => {
  const normalizedTitle = normalizeString(title);

  try {
    const response = await axios.get(`${BASE_URL_API}search/movie?api_key=${API_KEY}&language=es-MX&query=${encodeURIComponent(normalizedTitle)}`);
    let movies = response.data.results;

    // Filtrar por género si se proporciona
    if (genre) {
      const genreId = genreMap[genre.toLowerCase()];
      if (genreId) {
        movies = movies.filter(movie => movie.genre_ids.includes(genreId));
      } else {
        throw new Error("Género no encontrado.");
      }
    }

    // Ordenar por el criterio especificado
    if (sortBy === 'release_date.desc') {
      movies.sort((a, b) => new Date(b.release_date || '1970-01-01') - new Date(a.release_date || '1970-01-01'));
    } else if (sortBy === 'release_date.asc') {
      movies.sort((a, b) => new Date(a.release_date || '1970-01-01') - new Date(b.release_date || '1970-01-01'));
    }

    const moviesWithGenresAndRatings = await Promise.all(movies.map(async (movie) => {
      const detailsResponse = await axios.get(`${BASE_URL_API}movie/${movie.id}?api_key=${API_KEY}&language=es-MX&append_to_response=videos`);
      const detailedMovie = detailsResponse.data;

      // Obtener los nombres de los géneros
      const genres = detailedMovie.genres.map(genre => genre.name);
      
      // Mapear el nombre de género al ID usando genreMap
      const genreIds = detailedMovie.genres.map(genre => genreMap[genre.name.toLowerCase()]).filter(id => id !== undefined);

      // Buscar todas las calificaciones más recientes de otros usuarios para la misma película
      const users = await User.find({ 'ratings.movieId': movie.id });
      const latestRatings = users.flatMap(user => {
        const ratingsForMovie = user.ratings.filter(r => r.movieId === String(movie.id));
        if (ratingsForMovie.length > 0) {
          const latestRating = ratingsForMovie[ratingsForMovie.length - 1].rating;
          return [latestRating];
        }
        return [];
      });

      // Calcular el promedio de las calificaciones más recientes
      const averageRating = latestRatings.length > 0 ? latestRatings.reduce((sum, rating) => sum + rating, 0) / latestRatings.length : null;

      return {
        id: detailedMovie.id,
        poster: detailedMovie.poster_path ? `https://image.tmdb.org/t/p/w500${detailedMovie.poster_path}` : null,
        title: detailedMovie.title,
        duracion: detailedMovie.runtime,
        fecha_lanzamiento: detailedMovie.release_date ? detailedMovie.release_date : 'sin fecha',
        genero: genres.join(", "),
        descripcion: detailedMovie.overview,
        trailer: detailedMovie.videos.results.length > 0 ? `https://www.youtube.com/watch?v=${detailedMovie.videos.results[0].key}` : null,
        genreIds: genreIds,
        averageRating: averageRating // Agregar el promedio de calificaciones recientes
      };
    }));

    // Guardar la búsqueda reciente del usuario
    const user = await User.findById(userId);
    if (user) {
      // Agregar la nueva búsqueda al inicio del array
      user.recentSearches.unshift({ query: normalizedTitle });
      // Limitar la lista a las últimas 5 búsquedas
      user.recentSearches = user.recentSearches.slice(0, 5);
      await user.save();
    }

    return moviesWithGenresAndRatings;
  } catch (error) {
    throw new Error("Ocurrió un error al buscar la película. Por favor, inténtalo de nuevo.");
  }
};






const getGenreId = (genreName) => {
    const genreId = genreMap[genreName.toLowerCase()];
    if (!genreId) {
      throw new Error("Género no encontrado.");
    }
    return genreId;
  };
  

const getMoviesCategory = async (categoryName, limit = 10) => {
  try {
    const categoryId = getGenreId(categoryName);
    const response = await axios.get(
      `${BASE_URL_API}discover/movie?api_key=${API_KEY}&language=es-MX&with_genres=${categoryId}&page=1&include_adult=false&sort_by=popularity.desc&vote_count.gte=1000&vote_average.gte=5&with_watch_monetization_types=flatrate`
    );

    const movies = response.data.results.slice(0, limit); // Limitar las películas al número especificado





    // Enrich each movie with additional details including actors
    const moviesWithDetails = await Promise.all(
      movies.map(async (movie) => {
        // Obtener detalles de la película
        const detailsResponse = await axios.get(
          `${BASE_URL_API}movie/${movie.id}?api_key=${API_KEY}&language=es-MX&append_to_response=credits,videos`
        );
        const detailedMovie = detailsResponse.data;

        // Mapear los nombres y fotos de los actores
        const actors = detailedMovie.credits.cast.map((actor) => ({
          name: actor.name,
          profileUrl: actor.profile_path
            ? `https://image.tmdb.org/t/p/w500${actor.profile_path}`
            : null,
        }));

        return {
          id: detailedMovie.id,
          poster: detailedMovie.poster_path
            ? `https://image.tmdb.org/t/p/w500${detailedMovie.poster_path}`
            : null,
          title: detailedMovie.title,
          duracion: detailedMovie.runtime,
          fecha_lanzamiento: detailedMovie.release_date,
          genero: detailedMovie.genres.map((genre) => genre.name).join(", "),
          descripcion: detailedMovie.overview,
          trailer:
            detailedMovie.videos.results.length > 0
              ? `https://www.youtube.com/watch?v=${detailedMovie.videos.results[0].key}`
              : null,
          actors: actors,
        };
      })
    );




    
    return moviesWithDetails.sort(() => Math.random() - 0.5); // Reordenar aleatoriamente las películas
  } catch (error) {
    throw new Error(`No se ha podido encontrar películas en la categoría: ${categoryName}`);
  }
};







//mas bisots/recientess
const getMoviesBySortType = async (sortType, limit = 10) => {
  try {
    const genreIds = Object.values(genreMap);
    let allMovies = [];

    for (const genreId of genreIds) {
      const response = await axios.get(`${BASE_URL_API}discover/movie?api_key=${API_KEY}&language=es-MX&with_genres=${genreId}&page=1&include_adult=false&sort_by=${sortType}&vote_count.gte=1000&vote_average.gte=5&with_watch_monetization_types=flatrate`);
      const movies = response.data.results.slice(0, limit);

      // Enrich each movie with additional details
      const moviesWithDetails = await Promise.all(movies.map(async (movie) => {
        const detailsResponse = await axios.get(`${BASE_URL_API}movie/${movie.id}?api_key=${API_KEY}&language=es-MX&append_to_response=videos,credits`); // Agregar credits para obtener los actores
        const detailedMovie = detailsResponse.data;

        // Obtener nombres y fotos de los actores
        const actors = detailedMovie.credits.cast.map(actor => ({
          name: actor.name,
          profilePhoto: actor.profile_path ? `https://image.tmdb.org/t/p/w500${actor.profile_path}` : null
        }));

        return {
          id: detailedMovie.id,
          poster: detailedMovie.poster_path ? `https://image.tmdb.org/t/p/w500${detailedMovie.poster_path}` : null,
          title: detailedMovie.title,
          duracion: detailedMovie.runtime,
          fecha_lanzamiento: detailedMovie.release_date,
          genero: detailedMovie.genres.map(genre => genre.name).join(", "),
          descripcion: detailedMovie.overview,
          trailer: detailedMovie.videos.results.length > 0 ? `https://www.youtube.com/watch?v=${detailedMovie.videos.results[0].key}` : null,
          actors: actors // Agregar información de los actores al objeto de la película
        };
      }));

      allMovies = allMovies.concat(moviesWithDetails);
    }

    return allMovies;
  } catch (error) {
    throw new Error("No se ha podido encontrar las películas.");
  }
};


//mas vistos
  const getMostViewedMovies = async (limit = 10) => {
    return getMoviesBySortType('popularity.desc', limit);
  };
  
  //mas recietnes
  const getMostRecentMovies = async (limit = 10) => {
    return getMoviesBySortType('release_date.desc', limit);
  };
  



  const fetchMovieById = async (id,userId) => {
    try {
      const response = await axios.get(`${BASE_URL_API}movie/${id}?api_key=${API_KEY}&language=es-MX&append_to_response=videos,credits`);
      const movie = response.data;
  
      const actors = movie.credits.cast.map(actor => ({
        name: actor.name,
        profileUrl: actor.profile_path ? `https://image.tmdb.org/t/p/w500${actor.profile_path}` : null,
      }));

   //   Buscar la calificación del usuario para esta película
  
    const user = await User.findById(userId);
    let userRating = null;

    if (user) {
      const userRatingObj = user.ratings.find(rating => rating.movieId === id.toString());
      if (userRatingObj) {
        userRating = userRatingObj.rating;
      }
    }

  
      return {
        id: movie.id,
        poster: movie.poster_path ? `https://image.tmdb.org/t/p/w500${movie.poster_path}` : null,
        title: movie.title,
        duracion: movie.runtime,
        fecha_lanzamiento: movie.release_date,
        genero: movie.genres.map(genre => genre.name).join(", "),
        descripcion: movie.overview,
        trailer: movie.videos.results.length > 0 ? `https://www.youtube.com/watch?v=${movie.videos.results[0].key}` : null,
        actors: actors,
        userRating: userRating ? userRating :0 // Incluir la calificación del usuario
      };
    } catch (error) {
      throw new Error("Error occurred while fetching movie details. Please try again.");
    }
  };
  


  //calificaciones generales
  // const getMovieAverageRating = async (movieId) => {
  //   try {
  //     // Encontrar todos los usuarios que hayan calificado la película
  //     const users = await User.find({ 'ratings.movieId': movieId });
  
  //     if (users.length === 0) {
  //       return null; // Si no hay usuarios que hayan calificado la película, retornar null
  //     }
  
  //     // Filtrar las calificaciones de la película específica
  //     const ratings = users.map(user => {
  //       const ratingObj = user.ratings.find(r => r.movieId === movieId);
  //       return ratingObj ? ratingObj.rating : null;
  //     }).filter(r => r !== null);
  
  //     if (ratings.length === 0) {
  //       return null; // Si no hay calificaciones encontradas, retornar null
  //     }
  
  //     // Calcular el promedio de las calificaciones
  //     const averageRating = ratings.reduce((sum, rating) => sum + rating, 0) / ratings.length;
  
  //     // Obtener detalles de la película desde cualquier usuario que la haya calificado
  //     const user = users.find(user => user.ratings.find(r => r.movieId === movieId));
  //     const movieDetails = user ? user.ratings.find(r => r.movieId === movieId) : null;
  
  //     return {
  //       averageRating,
  //       movieDetails
  //     };
  //   } catch (error) {
  //     throw new Error("Error occurred while calculating the average rating. Please try again.");
  //   }
  // };
  
  







//obtener peliculas ya calificadas(usuario)
const getMovieDetailsFromAPI = async (movieId) => {
  try {
    const response = await axios.get(`${BASE_URL_API}movie/${movieId}?api_key=${API_KEY}&language=es-MX`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching movie details for movieId ${movieId}:`, error);
    return null;
  }
};

//obtener peliculas ya calificadas(usuario)

const getRatedMovies = async (userId) => {
  try {
    const user = await User.findById(userId);

    if (!user || !user.ratings || user.ratings.length === 0) {
      return [];
    }

    // Crear un objeto para almacenar las películas calificadas por el usuario
    const ratedMovies = {};

    // Iterar sobre las calificaciones del usuario
    for (const rating of user.ratings) {
      if (!ratedMovies[rating.movieId]) {
        const movieDetails = await getMovieDetailsFromAPI(rating.movieId);
        if (movieDetails) {
          ratedMovies[rating.movieId] = {
            movieId: rating.movieId,
            title: movieDetails.title,
            poster: movieDetails.poster_path ? `https://image.tmdb.org/t/p/w500${movieDetails.poster_path}` : null,
            releaseDate: movieDetails.release_date,
            genres: movieDetails.genres.map(genre => genre.name).join(', '),
            ratings: [] // Aquí almacenaremos las calificaciones más recientes de todos los usuarios
          };
        }
      }

      // Obtener la calificación más reciente del usuario para esta película
      if (ratedMovies[rating.movieId]) {
        const latestRating = ratedMovies[rating.movieId].ratings.length > 0 ? ratedMovies[rating.movieId].ratings[0] : null;
        if (!latestRating || rating.timestamp > latestRating.timestamp) {
          ratedMovies[rating.movieId].ratings = [{
            rating: rating.rating,
            timestamp: rating.timestamp
          }];
        }
      }
    }

    // Convertir el objeto a un array y calcular el promedio de calificaciones para cada película
    const ratedMoviesArray = Object.values(ratedMovies);

    for (const movie of ratedMoviesArray) {
      // Buscar todas las calificaciones más recientes de otros usuarios para la misma película
      const users = await User.find({ 'ratings.movieId': movie.movieId });
      const latestRatings = users.flatMap(user => {
        const ratingsForMovie = user.ratings.filter(r => r.movieId === movie.movieId);
        if (ratingsForMovie.length > 0) {
          const latestRating = ratingsForMovie[ratingsForMovie.length - 1].rating;
          return [latestRating];
        }
        return [];
      });

      // Calcular el promedio de las calificaciones más recientes
      const averageRating = latestRatings.length > 0 ? latestRatings.reduce((sum, rating) => sum + rating, 0) / latestRatings.length : null;
      movie.averageRating = averageRating;
    }

    console.log('Películas calificadas por el usuario:', ratedMoviesArray);

    return ratedMoviesArray;
  } catch (error) {
    console.error("Error in getRatedMovies:", error);
    throw new Error("Error occurred while fetching rated movies. Please try again.");
  }
};





  //proxiomo a estrenar
  const getUpcomingMovies= async (limit = 10) => {
    try {
      const today = new Date().toISOString().split("T")[0]; // Fecha actual en formato ISO
      const response = await axios.get(`${BASE_URL_API}discover/movie`, {
        params: {
          api_key: API_KEY,
          language: "es-MX",
          primary_release_date_gte: today,
          sort_by: "primary_release_date.asc",
          include_adult: false,
          page: 1,
        },
      });
  
      const upcomingMovies = response.data.results.slice(0, limit);
  
      const moviesWithDetails = await Promise.all(
        upcomingMovies.map(async (movie) => {
          const detailedMovieResponse = await axios.get(
            `${BASE_URL_API}movie/${movie.id}`,
            {
              params: {
                api_key: API_KEY,
                language: "es-MX",
                append_to_response: "videos",
              },
            }
          );
          const detailedMovie = detailedMovieResponse.data;
  
          return {
            id: detailedMovie.id,
            poster: detailedMovie.poster_path
              ? `https://image.tmdb.org/t/p/w500${detailedMovie.poster_path}`
              : null,
            title: detailedMovie.title,
            duracion: detailedMovie.runtime,
            fecha_lanzamiento: detailedMovie.release_date
              ? detailedMovie.release_date
              : "sin fecha",
            genero: detailedMovie.genres.map((genre) => genre.name).join(", "),
            descripcion: detailedMovie.overview,
            trailer:
              detailedMovie.videos.results.length > 0
                ? `https://www.youtube.com/wat ch?v=${detailedMovie.videos.results[0].key}`
                : null,
          };
        })
      );
  
      return moviesWithDetails;
    } catch (error) {
      console.error("Error en getUpcomingMoviesAPI:", error.message);
      throw new Error(
        "Ocurrió un error al buscar los próximos estrenos. Por favor, inténtalo de nuevo."
      );
    }
  };



  const addToWishlist = async (userId, movieId) => {
    try {
      const movieDetails = await fetchMovieById(movieId);
      const user = await User.findById(userId);
  
      if (!user) {
        throw new Error('Usuario no encontrado');
      }
  
      const alreadyInWishlist = user.wishlist.some(item => item.movieId === movieDetails.id);
  
      if (alreadyInWishlist) {
        throw new Error('La película ya está en la lista de deseos');
      }
  
      user.wishlist.push({
        movieId: movieDetails.id,
        title: movieDetails.title,
        poster: movieDetails.poster,
        releaseDate: movieDetails.fecha_lanzamiento, // Usar fecha de lanzamiento del objeto movieDetails
        genre: movieDetails.genero //
        
      });
  
      await user.save();
  
      return { message: 'Película añadida a la lista de deseos' };
    } catch (error) {
      throw new Error(error.message);
    }
  };
  


  //obtener watclist
  const getWishlist = async (userId) => {
    try {
      const user = await User.findById(userId);
  
      if (!user) {
        throw new Error('Usuario no encontrado');
      }
  
      return user.wishlist;
    } catch (error) {
      throw new Error(error.message);
    }
  };
  
  
  const searchActorsByName = async (actorName) => {
    try {
      const response = await axios.get(
        `${BASE_URL_API}search/person?api_key=${API_KEY}&language=es-MX&query=${encodeURIComponent(actorName)}`
      );
  
      const actors = response.data.results.map(actor => ({
        name: actor.name,
        profileUrl: actor.profile_path ? `https://image.tmdb.org/t/p/w500${actor.profile_path}` : null
      }));
  
      return actors;
    } catch (error) {
      console.error("Error al buscar actores:", error.message);
      throw new Error("Ocurrió un error al buscar actores. Por favor, inténtalo de nuevo.");
    }
  };
  


  const getUserRatingsForMovie = async (userId, movieId) => {
    try {
      const user = await User.findById(userId);
  
      if (!user) {
        throw new Error('Usuario no encontrado');
      }
  
      // Asegurémonos de que movieId es una cadena
      movieId = movieId.toString();
      
      console.log(`User ID: ${userId}`);
      console.log(`Movie ID: ${movieId}`);
      console.log('User Ratings:', user.ratings);
  
      const ratingsForMovie = user.ratings
        .filter(rating => rating.movieId.toString() === movieId);
  
      console.log('Filtered Ratings for Movie:', ratingsForMovie);
  
      if (ratingsForMovie.length > 0) {
        const sortedRatings = ratingsForMovie.sort((a, b) => {
          console.log(`Comparing ${a.timestamp} and ${b.timestamp}`);
          return new Date(b.timestamp) - new Date(a.timestamp);
        });
        
        console.log('Sorted Ratings:', sortedRatings);
  
        const userRatingObj = sortedRatings[0]; // Obtener la calificación más reciente
  
        return {
          userId: user._id,
          rating: userRatingObj.rating
        };
      } else {
        console.log('No ratings found for the movie.');
        return null; // Si no hay calificaciones para la película
      }
    } catch (error) {
      console.error('Error:', error);
      throw new Error('Error al obtener las calificaciones del usuario para la película');
    }
  };
  
  const getMovieAverageRating = async (movieId) => {
    try {
      // Encuentra todos los usuarios que han calificado esta película
      const users = await User.find({ 'ratings.movieId': movieId });
  
      // Recoge la calificación más reciente de cada usuario para esta película
      const latestRatings = users.flatMap(user => {
        const ratingsForMovie = user.ratings.filter(r => r.movieId === movieId);
        if (ratingsForMovie.length > 0) {
          // Obtener la calificación más reciente
          const latestRating = ratingsForMovie[ratingsForMovie.length - 1].rating;
          return [latestRating];
        }
        return [];
      });
  
      // Verificar si hay calificaciones
      if (latestRatings.length === 0) {
        return null; // No hay calificaciones para esta película
      }
  
      // Calcula el promedio de las calificaciones
      const averageRating = latestRatings.reduce((sum, rating) => sum + rating, 0) / latestRatings.length;
      console.log(`Average rating calculated: ${averageRating}`);

      return averageRating;
    } catch (error) {
      throw new Error("Error occurred while calculating the average rating.");
    }
  };

// se exportan como se deven son variables por asi asi
module.exports={
    getMoviesAPI:getMovies,
    getMoviesCategoryAPI:getMoviesCategory,
      getMostViewedMoviesAPI: getMostViewedMovies,
  getMostRecentMoviesAPI: getMostRecentMovies,
  searchMovieByTitleAPI: searchMovieByTitle,
  fetchMovieByIdAPI:fetchMovieById,
  //getMovieAverageRatingAPI:getMovieAverageRating,
  getUpcomingMoviesAPI:getUpcomingMovies,
  addToWishlistAPI:addToWishlist,
  getWishlistAPI:getWishlist,
  getRatedMoviesAPI:getRatedMovies,
  hideMovieAPI: hideMovie,
  searchActorsByNameAPI:searchActorsByName,
  getUserRatingsForMovieAPI:getUserRatingsForMovie,
  getMovieAverageRatingAPI:getMovieAverageRating
}