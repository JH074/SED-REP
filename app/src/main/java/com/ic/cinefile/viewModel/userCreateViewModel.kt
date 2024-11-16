package com.ic.cinefile.viewModel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.gson.annotations.SerializedName
import com.ic.cinefile.API.Model.movies.AverageRatingResponse
import com.ic.cinefile.API.Model.movies.MovieAdmin
import com.ic.cinefile.API.Model.movies.ReplyComment
import com.ic.cinefile.API.Model.movies.actorNameResponse
import com.ic.cinefile.API.Model.movies.getCommentResponse
import com.ic.cinefile.API.Model.movies.homeUserResponse
import com.ic.cinefile.API.Model.movies.mostViewMoviesResponse
import com.ic.cinefile.API.Model.movies.movieResponseAdminResponse
import com.ic.cinefile.API.Model.movies.moviesResponse
import com.ic.cinefile.API.Model.movies.rateMoveResponse
import com.ic.cinefile.API.Model.movies.recentMoviesResponse
import com.ic.cinefile.API.Model.movies.searchMoviesResponse
import com.ic.cinefile.API.Model.movies.topMoviesResponse
import com.ic.cinefile.API.Model.movies.wishListResponse
import com.ic.cinefile.API.Model.users.NotificationResponse
import com.ic.cinefile.API.Model.users.UserLoginResponse
import com.ic.cinefile.API.apiServer
import com.ic.cinefile.Navigation.screenRoute
import com.ic.cinefile.R
import com.ic.cinefile.data.Actor
import com.ic.cinefile.data.NotificationData
import com.ic.cinefile.data.RatingData
import com.ic.cinefile.data.accountLoginData
import com.ic.cinefile.data.accountRegisterData
import com.ic.cinefile.data.commentData
import com.ic.cinefile.data.createMovieData
import com.ic.cinefile.data.editCommentData
import com.ic.cinefile.data.searchMoviesData
import com.ic.cinefile.data.witchListData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.text.Normalizer
import java.time.LocalDateTime
import java.util.Locale.Category
import java.util.concurrent.TimeUnit
import java.util.prefs.Preferences

class userCreateViewModel(
    private val userPreferences: UserPreferences
): ViewModel() {

    // Estados que gestionan el estado de la interfaz para login
    private val _uiState = MutableStateFlow<UiState>(UiState.Ready)
    val uiState : StateFlow<UiState> = _uiState

    private val _uiState2 = MutableStateFlow<UiState2>(UiState2.Ready)
    val uiState2 : StateFlow<UiState2> = _uiState2


    // Estados que gestionan la informacion de las screens
    private val _accountCreateAPI = mutableStateOf(accountRegisterData())
    val accountcreateAPIData: State<accountRegisterData> = _accountCreateAPI

    private val _accountLoginAPI = mutableStateOf(accountLoginData())
    val accountLoginAPIData: State<accountLoginData> = _accountLoginAPI


    private val _userDataState = MutableStateFlow<UserDataState>(UserDataState.Ready)
    val userDataState: StateFlow<UserDataState> = _userDataState

    // Estado para manejar las películas más recientes
    private val _recentMoviesState = MutableStateFlow<RecentMoviestState>(RecentMoviestState.Ready)
    val recentMoviesState: StateFlow<RecentMoviestState> = _recentMoviesState

    // Estado para manejar las películas más recientes
    private val _mostViewsMoviesState = MutableStateFlow<MostViewsMoviestState>(MostViewsMoviestState.Ready)
    val mostViewsMoviesState: StateFlow<MostViewsMoviestState> = _mostViewsMoviesState

    // Estado para manejar los detalles de la película
    private val _movieState = MutableStateFlow<MovieState>(MovieState.Ready)
    val movieState: StateFlow<MovieState> = _movieState

    //Estado para manejar las los comentarios  enviados
    private val _postCommentState = mutableStateOf(commentData())
    val postCommentState: State<commentData> = _postCommentState

    //Estado para manejar las respuestas de los comentarios
    private val _repliesToCommentState = MutableStateFlow<RepliesToCommentState>(RepliesToCommentState.Ready)
    val repliesToCommentState: StateFlow<RepliesToCommentState> = _repliesToCommentState

    // Estado para manejar los comentarios obtenidos
    private val _commentsState = MutableStateFlow<CommentListState>(CommentListState.Ready)
    val commentsState: StateFlow<CommentListState> = _commentsState

    // Estado de busquedas recientes
    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches

    //estado de buscar pelicula por el titulo
    private val _searchState = MutableStateFlow<SearchState>(SearchState.Ready)
    val searchState: StateFlow<SearchState> = _searchState

    private val _notificationState = MutableStateFlow<NotificationState>(NotificationState.Ready)
    val notificationState: StateFlow<NotificationState> = _notificationState

    private val _markNotificationState = MutableStateFlow<MarkNotificationState>(MarkNotificationState.Ready)
    val markNotificationState: StateFlow<MarkNotificationState> = _markNotificationState

    // Estados que gestionan el estado de la interfaz para calificaciones
    private val _rateMovieState = MutableStateFlow<RateMovieState>(RateMovieState.Ready)
    val rateMovieState: StateFlow<RateMovieState> = _rateMovieState

    private val _averageRatingState = MutableStateFlow<AverageRatingState>(AverageRatingState.Ready)
    private val _averageRatingForUserState = MutableStateFlow<AverageRatingForUserState>(AverageRatingForUserState.Ready)

    private val _userRatingState = MutableStateFlow<UserRatingState>(UserRatingState.Ready)
    val userRatingState: StateFlow<UserRatingState> = _userRatingState

    //peliculas en general
    val averageRatingState: StateFlow<AverageRatingState> get() = _averageRatingState
    val averageRatingForUserState: StateFlow<AverageRatingForUserState> get() = _averageRatingForUserState

    //top peliculas
    private val _topMoviesState = MutableStateFlow<TopMoviestState>(TopMoviestState.Ready)
    val topMoviesState: StateFlow<TopMoviestState> = _topMoviesState

    private val _wishListGetState = MutableStateFlow<WishlistGetState>(WishlistGetState.Ready)
    val wishlistState: StateFlow<WishlistGetState> = _wishListGetState

    private val _wishlistState = MutableStateFlow<WishlistGetState>(WishlistGetState.Ready)
    val wishlisGetState : StateFlow<WishlistGetState> = _wishlistState

    private val _wishListPostState = mutableStateOf(witchListData())
    val wishListPostState: State<witchListData> = _wishListPostState


    //obtener peliculas individual
    private val _moviesReated = MutableStateFlow<MoviesReated>(MoviesReated.Ready)
    val moviesReatedState : StateFlow<MoviesReated> = _moviesReated

    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> get() = _authToken


    init {
        viewModelScope.launch {
            userPreferences.authToken.collect { token ->
                val receivedTime = userPreferences.tokenReceivedTime.first()
                val currentTime = System.currentTimeMillis()
                val oneDayInMillis = TimeUnit.DAYS.toMillis(1)

                if (token != null && receivedTime != null && (currentTime - receivedTime) < oneDayInMillis) {
                    _authToken.value = token
                    userRole = userPreferences.userRole.first()
                    fetchUserData()
                } else {
                    _authToken.value = null
                    userRole = null
                }
            }
        }
    }

    private var userRole: String? = null // Propiedad para almacenar el rol del usuario

    private val _deleteCommentState = MutableStateFlow<DeleteCommentState>(DeleteCommentState.Ready)
    val deleteCommentState: StateFlow<DeleteCommentState> = _deleteCommentState

    private val _deleteReplyState = MutableStateFlow<DeleteReplyState>(DeleteReplyState.Ready)
    val deleteReplyState: StateFlow<DeleteReplyState> = _deleteReplyState



    //ADMIN
    private val _createMovie = mutableStateOf(createMovieData())
    val createMovie: State<createMovieData> = _createMovie

    private val _getMovieCreate = MutableStateFlow<GetMovieCreate>(GetMovieCreate.Ready)
    val getMovieCreate: StateFlow<GetMovieCreate> = _getMovieCreate

    //peliculas creadas por id
    private val _getMovieCreateState = MutableStateFlow<GetMovieCreateState>(GetMovieCreateState.Ready)
    val getMovieCreateState: StateFlow<GetMovieCreateState> = _getMovieCreateState

    // Estado para la búsqueda de actores por nombre
    private val _searchActorsState = MutableStateFlow<SearchActorsState>(SearchActorsState.Ready)
    val searchActorsState: StateFlow<SearchActorsState> get() = _searchActorsState


    private val _selectedActors = mutableStateListOf<Actor>()
    val selectedActors: List<Actor> get() = _selectedActors

    private val _logoutResult = MutableStateFlow<LogoutResult?>(null)
    val logoutResult: StateFlow<LogoutResult?> get() = _logoutResult

    private val _editCommentState = MutableStateFlow<EditCommentState>(EditCommentState.Ready)
    val editCommentState: StateFlow<EditCommentState> = _editCommentState



    fun updateAccountData(newData: accountRegisterData) {
        _accountCreateAPI.value = newData
        Log.i("userCreateViewModel", "Updated data: $newData")

    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            // Cambiar el estado a Loading mientras se realiza la operación
            _logoutResult.value = LogoutResult.Loading

            _authToken.value?.let { token ->
                try {
                    // Realizar la llamada a la función de logout
                    val response = apiServer.methods.logoutAccount("Bearer $token")

                    // Verificar si la respuesta fue exitosa
                    if (response.isSuccessful) {
                        // Borrar el token de UserPreferences
                        userPreferences.clear()
                        _authToken.value = null

                        // Actualizar el estado a Success si el logout fue exitoso
                        _logoutResult.value = LogoutResult.Success
                    } else {
                        // Manejar errores de respuesta no exitosa
                        val errorBody = response.errorBody()?.string()
                        val errorMsg = errorBody ?: "Unknown error"
                        _logoutResult.value = LogoutResult.Error(errorMsg)
                    }
                } catch (e: HttpException) {
                    // Manejar errores de red (por ejemplo, error 404, 500, etc.)
                    _logoutResult.value = LogoutResult.Error("Network Error: ${e.message()}")
                } catch (e: Exception) {
                    // Manejar otros errores inesperados
                    _logoutResult.value = LogoutResult.Error("Unexpected Error: ${e.message}")
                }
            } ?: run {
                // Si el token es nulo, manejar el caso adecuadamente
                _logoutResult.value = LogoutResult.Error("Token no disponible")
            }
        }
    }



    fun updateMovieGenres(newGenres: List<String>) {
        _accountCreateAPI.value = _accountCreateAPI.value.copy(
            movieGenereList = newGenres.toMutableList()
        )
        Log.i("userCreateViewModel", "Updated data: ${_accountCreateAPI.value.movieGenereList}")
    }




    // Función para agregar un actor a la lista de actores

    fun createAccountUser(userregisterData: accountRegisterData){

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState2.value = UiState2.Loading
                val response = apiServer.methods.createAccount(userregisterData)
                Log.i("userCreateViewModel", response.toString())

                _uiState2.value = UiState2.Success("Uusario creado correctamente")

            }catch (e:Exception){
                when (e) {
                    is HttpException -> {
                        Log.i("userCreateViewModel", e.message())
                        _uiState2.value = UiState2.Error( e.message())
                    }
                    else -> {
                        Log.i("userCreateViewModel", e.toString())
                        _uiState2.value = UiState2.Error( "Error. Contacte con el servicio de soporte")

                    }
                }
            }
        }

    }


    fun setStateToReady() {
        _uiState.value = UiState.Ready
    }

    //iniciar sesion
    private val _showErrorToast = MutableStateFlow(false)
    val showErrorToast = _showErrorToast.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()

    fun loginUser(userLoginData: accountLoginData) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.value = UiState.Loading
                val response = apiServer.methods.loginAccount(userLoginData)
                Log.i("userLoginViewModel", response.toString())

                val token = response.token
                val role = response.role

                // Guardar el token y el rol del usuario en UserPreferences
                userPreferences.saveAuthToken(token)
                userPreferences.saveUserRole(role)

                // Actualizar las variables del ViewModel
                _authToken.value = token
                userRole = role

                // Cambiar el estado a Success
                _uiState.value = UiState.Success(token)

                // Obtener información del usuario utilizando el token
                fetchUserData()


            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        Log.i("userCreateViewModel", e.message())
                        _uiState.value = UiState.Error(e.message())
                    }
                    else -> {
                        Log.i("userCreateViewModel", e.toString())
                        _uiState.value = UiState.Error("Verifica su conexion, Intentalo mas tarde")
                    }
                }
            }
        }
    }

    fun getUserRole(): String {
        return userRole ?:""
    }

    private fun setErrorMessage(message: String) {
        _errorMessage.value = message
        _showErrorToast.value = true
    }

    fun hideErrorToast() {
        _showErrorToast.value = false
    }



    //manejar token
    fun fetchUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            _authToken.value?.let { token ->
                try {
                    _userDataState.value = UserDataState.Loading
                    val response = apiServer.methods.getUserHome("Bearer $token")
                    if (response.isSuccessful) {
                        val userData = response.body()
                        _userDataState.value = userData?.let { UserDataState.Success(it) }
                            ?: UserDataState.Error("Error: Datos del usuario no encontrados")
                    } else {
                        _userDataState.value = UserDataState.Error("Error: ${response.message()}")
                    }
                } catch (e: Exception) {
                    when (e) {
                        is HttpException -> {
                            Log.i("userCreateViewModel", e.message())
                            _userDataState.value = UserDataState.Error(e.message())
                        }
                        else -> {
                            Log.i("userCreateViewModel", e.message.toString())
                            _userDataState.value = UserDataState.Error(e.message ?: "Error desconocido")
                        }
                    }
                }
            } ?: run {
                _userDataState.value = UserDataState.Error("Token no disponible")
            }
        }
    }




    var showReloadButton by mutableStateOf(false)
        private set

    // Función para mostrar el botón de recarga después de 7 segundos
    fun startLoadingTimer() {
        viewModelScope.launch {
            delay(7000)
            showReloadButton = true
        }
    }

    fun resetReloadButton() {
        showReloadButton = false
    }


    // Función para recargar datos y ocultar el botón de recarga
    fun reload() {
        showReloadButton = false
        _uiState.value = UiState.Loading

        // Obtener el token desde UserPreferences
        viewModelScope.launch {
            val token = userPreferences.authToken.firstOrNull()
            if (token != null) {
                fetchUserData()
            } else {
                _uiState.value = UiState.Error("Token no disponible")
            }
        }
    }

    fun reloadUserData() {
        showReloadButton = false
        _uiState.value = UiState.Loading

        // Obtener el token desde UserPreferences
        viewModelScope.launch {
            try {
                val token = userPreferences.authToken.firstOrNull()
                if (token != null) {
                    fetchUserData()
                    _uiState.value = UiState.Success(token)
                } else {
                    _uiState.value = UiState.Error("Token no disponible")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error desconocido")
            }
        }
    }


    //obtener peliculas recientes
    fun getRecentMoviesData() {
        viewModelScope.launch(Dispatchers.IO) {
            _authToken.value?.let { token ->
                try {
                    _recentMoviesState.value = RecentMoviestState.Loading
                    val response = apiServer.methods.getRecentMovies("Bearer $token")
                    if (response.isSuccessful) {
                        val data = response.body()
                        val uniqueMovies = data?.moviesRecent?.distinctBy { it.id } // Filtra duplicados por ID
                        _recentMoviesState.value = uniqueMovies?.let { RecentMoviestState.Success(recentMoviesResponse(it)) } ?: RecentMoviestState.Error("Error: Peliculas no encontradas")
                    } else {
                        _recentMoviesState.value = RecentMoviestState.Error("Error: ${response.message()}")
                    }
                } catch (e: Exception) {
                    when (e) {
                        is HttpException -> {
                            Log.i("userCreateViewModel", e.message())
                            _recentMoviesState.value = RecentMoviestState.Error(e.message())
                        }
                        else -> {
                            Log.i("userCreateViewModel", e.message.toString())
                            _recentMoviesState.value = RecentMoviestState.Error(e.message ?: "Error desconocido")
                        }
                    }
                }
            } ?: run {
                _recentMoviesState.value = RecentMoviestState.Error("Token no disponible")
            }
        }
    }



    //obtener peliculas mas vistas
    fun getMostViewMoviesData() {
        viewModelScope.launch(Dispatchers.IO) {
            _authToken.value?.let { token ->
                try {
                    _mostViewsMoviesState.value = MostViewsMoviestState.Loading
                    val response = apiServer.methods.getMostViewMovies("Bearer $token")
                    if (response.isSuccessful) {
                        val data = response.body()
                        val uniqueMovies = data?.moviesMostViews?.distinctBy { it.id } // Filtra duplicados por ID
                        _mostViewsMoviesState.value = uniqueMovies?.let { MostViewsMoviestState.Success(mostViewMoviesResponse(it)) } ?: MostViewsMoviestState.Error("Error: Peliculas no encontradas")
                    } else {
                        _mostViewsMoviesState.value = MostViewsMoviestState.Error("Error: ${response.message()}")
                    }
                } catch (e: Exception) {
                    when (e) {
                        is HttpException -> {
                            Log.i("userCreateViewModel", e.message())
                            _mostViewsMoviesState.value = MostViewsMoviestState.Error(e.message())
                        }
                        else -> {
                            Log.i("userCreateViewModel", e.message.toString())
                            _mostViewsMoviesState.value = MostViewsMoviestState.Error(e.message ?: "Error desconocido")
                        }
                    }
                }
            } ?: run {
                _mostViewsMoviesState.value = MostViewsMoviestState.Error("Token no disponible")
            }
        }
    }



    //obtener por id
    fun getMovieById( movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _authToken.value?.let { token ->

                try {
                    _movieState.value = MovieState.Loading
                    val response = apiServer.methods.getMovieById("Bearer $token", movieId)
                    if (response.isSuccessful) {
                        response.body()?.let { movie ->
                            _movieState.value = MovieState.Success(movie)
                        } ?: run {
                            _movieState.value = MovieState.Error("Movie not found")
                        }
                    } else {
                        _movieState.value =
                            MovieState.Error("Error fetching movie: ${response.message()}")
                    }
                } catch (e: Exception) {
                    when (e) {
                        is HttpException -> {
                            Log.e("UserCreateViewModel", e.message())
                            _movieState.value =
                                MovieState.Error("Error fetching movie: ${e.message()}")
                        }

                        else -> {
                            Log.e("UserCreateViewModel", e.toString())
                            _movieState.value =
                                MovieState.Error("Error fetching movie: ${e.message}")
                        }
                    }
                }
            }
        }
    }


    fun searchMoviesByTitle(title: String, sortBy: String? = null, genre: String? = null) {
        val normalizedTitle = normalizeString(title)
        resetReloadButton()
        startLoadingTimer()

        viewModelScope.launch(Dispatchers.IO) {
            _authToken.value?.let { token ->

            try {
                _searchState.value = SearchState.Loading
                val response = apiServer.methods.searchMovies(
                    "Bearer $token",
                    normalizedTitle,
                    sortBy,
                    genre
                )
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        if (responseBody is searchMoviesResponse) {
                            var movies =
                                responseBody.moviesSearch // Suponiendo que `movies` es una lista de películas

                            // Aplicar filtro por género si se especifica
                            if (!genre.isNullOrEmpty()) {
                                movies = movies.filter { movie ->
                                    movie.genres.contains(genre)
                                }
                            }

                            // Ordenar según el criterio seleccionado
                            if (!sortBy.isNullOrEmpty()) {
                                movies = when (sortBy.toLowerCase()) {
                                    "recientes" -> movies.sortedByDescending {
                                        it.releaseDate ?: "1970-01-01"
                                    }

                                    "masviejas" -> movies.sortedBy {
                                        it.releaseDate ?: "1970-01-01"
                                    }

                                    else -> movies
                                }
                            }

                            _searchState.value = SearchState.Success(searchMoviesResponse(movies))
                            updateRecentSearches(title) // Actualizar búsquedas recientes
                        } else {
                            _searchState.value = SearchState.Error(
                                "Error: Expected a movie object but received something else.",
                                R.drawable.error500
                            )
                        }
                    } else {
                        _searchState.value =
                            SearchState.Error("Error: Empty response body.", R.drawable.error500)
                    }
                } else {
                    _searchState.value =
                        SearchState.Error("Error: ${response.message()}", R.drawable.error500)
                }
            } catch (e: Exception) {
                val errorImage = when (e) {
                    is IOException -> {
                        Log.e("UserCreateViewModel", "Error de red: ${e.message}")
                        R.drawable.error404
                    }

                    is HttpException -> {
                        Log.e("UserCreateViewModel", "Error HTTP: ${e.message}")
                        R.drawable.error500
                    }

                    else -> {
                        Log.e("UserCreateViewModel", "Error desconocido: ${e.message}")
                        R.drawable.error500
                    }
                }
                _searchState.value = SearchState.Error("Error: ${e.message}", errorImage)
            }
        }
        }
    }

    fun normalizeString(str: String): String {
        return Normalizer.normalize(str, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .toLowerCase()
    }




    private fun updateRecentSearches(title: String) {
        val currentList = _recentSearches.value.toMutableList()
        if (!currentList.contains(title)) {
            currentList.add(0, title) // Agregar solo el título
            _recentSearches.value = currentList.take(5) // Limitar a los últimos 5 títulos
        }
    }
    //publicar comentario
    // Función para publicar un comentario
    fun postComment(movieId: Int, commentData: commentData, parentId: String? = null) {

        viewModelScope.launch(Dispatchers.IO) {
            _authToken.value?.let { token ->

            try {
                val response = apiServer.methods.postComment(
                    "Bearer $token",
                    movieId,
                    parentId ?: "",
                    commentData
                )
                if (response.isSuccessful) {
                    getComments(movieId)
                    _commentsState.value =
                        CommentListState.Success(emptyList()) // Puedes actualizar el estado según tu lógica de UI
                } else {
                    _commentsState.value = CommentListState.Error("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        Log.e("userCreateViewModel", "Error HTTP: ${e.message()}")
                        _commentsState.value = CommentListState.Error("Error HTTP: ${e.message()}")
                    }

                    else -> {
                        Log.e("userCreateViewModel", "Error: ${e.message}")
                        _commentsState.value = CommentListState.Error("Error: ${e.message}")
                    }
                }
            }
        }
        }
    }


    fun editComment(movieId: Int, commentId: String, updatedCommentText: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _authToken.value?.let { token ->
                try {
                    _editCommentState.value = EditCommentState.Loading

                    // Crear el cuerpo del comentario actualizado
                    val updatedComment = editCommentData(commentText = updatedCommentText)

                    // Llamada a la API
                    val response = apiServer.methods.editComment(
                        authorization = "Bearer $token",
                        movieId = movieId,
                        commentId = commentId,
                        updatedComment = updatedComment
                    )

                    if (response.isSuccessful) {
                        // Si la respuesta es exitosa, actualizar el estado a Success
                        val message = response.body()?.message ?: "Comentario actualizado con éxito"
                        _editCommentState.value = EditCommentState.Success(message)
                    } else {
                        // Si la respuesta falla, actualizar el estado a Error
                        _editCommentState.value =
                            EditCommentState.Error("Error: ${response.message()}")
                    }
                } catch (e: Exception) {
                    when (e) {
                        is HttpException -> {
                            // Manejo de errores HTTP
                            Log.e("userCreateViewModel", "Error HTTP: ${e.message()}")
                            _editCommentState.value =
                                EditCommentState.Error("Error HTTP: ${e.message()}")
                        }

                        else -> {
                            // Manejo de errores generales
                            Log.e("userCreateViewModel", "Error: ${e.message}")
                            _editCommentState.value =
                                EditCommentState.Error("Error: ${e.message}")
                        }
                    }
                }
            } ?: run {
                // Manejo del caso en el que no hay token disponible
                _editCommentState.value =
                    EditCommentState.Error("Token no disponible. Por favor, inicia sesión nuevamente.")
            }
        }
    }
    fun resetEditCommentState() {
        _editCommentState.value = EditCommentState.Ready
    }


    fun getComments(movieId: Int) {

        viewModelScope.launch(Dispatchers.IO) {
            _authToken.value?.let { token ->

                try {
                    _commentsState.value = CommentListState.Loading
                    val response = apiServer.methods.getComments("Bearer $token", movieId)
                    if (response.isSuccessful) {
                        val comments = response.body()
                        _commentsState.value = CommentListState.Success(comments ?: emptyList())
                    } else {
                        _commentsState.value =
                            CommentListState.Error("Error: ${response.message()}")
                    }
                } catch (e: Exception) {
                    when (e) {
                        is HttpException -> {
                            Log.e("userCreateViewModel", "Error HTTP: ${e.message()}")
                            _commentsState.value =
                                CommentListState.Error("Error HTTP: ${e.message()}")
                        }

                        else -> {
                            Log.e("userCreateViewModel", "Error: ${e.message}")
                            _commentsState.value = CommentListState.Error("Error: ${e.message}")
                        }
                    }
                }
            }
        }
    }

    // Nueva función para obtener comentarios en tiempo real
    fun pollComments(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _authToken.value?.let { token ->

                var lastFetched = System.currentTimeMillis()

                while (true) {
                    try {
                        _commentsState.value = CommentListState.Loading
                        val response = apiServer.methods.pollComments(
                            "Bearer $token",
                            movieId,
                            lastFetched.toString() // Convertir lastFetched a String
                        )

                        if (response.isSuccessful) {
                            val newComments = response.body()
                            _commentsState.value = CommentListState.Success(newComments ?: emptyList())
                            lastFetched = System.currentTimeMillis() // Actualiza el timestamp de la última búsqueda
                        } else {
                            _commentsState.value =
                                CommentListState.Error("Error: ${response.message()}")
                        }
                    } catch (e: Exception) {
                        when (e) {
                            is HttpException -> {
                                Log.e("CommentsViewModel", "Error HTTP: ${e.message()}")
                                _commentsState.value =
                                    CommentListState.Error("Error HTTP: ${e.message()}")
                            }
                            else -> {
                                Log.e("CommentsViewModel", "Error: ${e.message}")

                            }
                        }
                    }

                    delay(5000) // Espera 5 segundos antes de la siguiente solicitud
                }
            }
        }
    }


    fun getRepliesToComment(movieId: Int, parentId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _authToken.value?.let { token ->

                try {
                    _repliesToCommentState.value = RepliesToCommentState.Loading
                    //val response = apiServer.methods.getRepliesToComment("Bearer $authToken", movieId, parentId?: "" )
                    val response = apiServer.methods.getRepliesToComment(
                        "Bearer $token",
                        movieId,
                        parentId ?: ""
                    )

                    if (response.isSuccessful) {
                        val replies = response.body()
                        _repliesToCommentState.value =
                            RepliesToCommentState.Success(replies ?: emptyList())
                    } else {
                        _repliesToCommentState.value =
                            RepliesToCommentState.Error("Error: ${response.message()}")
                    }
                } catch (e: Exception) {
                    when (e) {
                        is HttpException -> {
                            Log.e("userCreateViewModel", "Error HTTP: ${e.message()}")
                            _repliesToCommentState.value =
                                RepliesToCommentState.Error("Error HTTP: ${e.message()}")
                        }

                        else -> {
                            Log.e("userCreateViewModel", "Error: ${e.message}")
                            _repliesToCommentState.value =
                                RepliesToCommentState.Error("Error: ${e.message}")
                        }
                    }
                }
            }
        }
    }

    // Enviar notificaciones
    fun getNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            _authToken.value?.let { token ->

                try {
                    _notificationState.value = NotificationState.Loading
                    val response = apiServer.methods.getNotifications("Bearer $token")
                    if (response.isSuccessful) {
                        val notifications = response.body()
                        _notificationState.value =
                            NotificationState.Success(notifications ?: emptyList())
                    } else {
                        _notificationState.value =
                            NotificationState.Error("Error: ${response.message()}")
                    }
                } catch (e: Exception) {
                    when (e) {
                        is HttpException -> {
                            Log.e("UserCreateViewModel", "Error HTTP: ${e.message()}")
                            _notificationState.value =
                                NotificationState.Error("Error HTTP: ${e.message()}")
                        }

                        else -> {
                            Log.e("UserCreateViewModel", "Error: ${e.message}")
                            _notificationState.value =
                                NotificationState.Error("Error: ${e.message}")
                        }
                    }
                }
            }
        }
    }

    fun markNotificationAsRead(notificationId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _markNotificationState.value = MarkNotificationState.Loading
                val response = apiServer.methods.markNotificationAsRead("Bearer $authToken", notificationId)
                if (response.isSuccessful) {
                    _markNotificationState.value = MarkNotificationState.Success("Notification marked as read")
                    getNotifications() // Refresh notifications after marking as read
                } else {
                    _markNotificationState.value = MarkNotificationState.Error("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        Log.e("UserCreateViewModel", "Error HTTP: ${e.message()}")
                        _markNotificationState.value = MarkNotificationState.Error("Error HTTP: ${e.message()}")
                    }
                    else -> {
                        Log.e("UserCreateViewModel", "Error: ${e.message}")
                        _markNotificationState.value = MarkNotificationState.Error("Error: ${e.message}")
                    }
                }
            }
        }
    }





    //calificar
    // Método para calificar una película
    fun rateMovie(movieId: Int, rating: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            _authToken.value?.let { token ->

                try {
                    _rateMovieState.value = RateMovieState.Loading
                    val ratingData = RatingData(movieId, rating)
                    val response = apiServer.methods.rateMovie("Bearer $token", movieId, ratingData)
                    if (response.isSuccessful) {
                        val message = response.body()?.message ?: "Movie rated successfully"
                        _rateMovieState.value = RateMovieState.Success(message)
                        _userRatingState.value = UserRatingState.Success(rating.toInt())

                        getAverageRating(movieId)
                    } else {
                        _rateMovieState.value = RateMovieState.Error("Error: ${response.message()}")
                    }
                } catch (e: Exception) {
                    when (e) {
                        is HttpException -> {
                            Log.e("userCreateViewModel", "Error HTTP: ${e.message()}")
                            _rateMovieState.value =
                                RateMovieState.Error("Error HTTP: ${e.message()}")
                        }

                        else -> {
                            Log.e("userCreateViewModel", "Error: ${e.message}")
                            _rateMovieState.value = RateMovieState.Error("Error: ${e.message}")
                        }
                    }
                }
            }
        }
    }


    // Método para cargar la calificación almacenada localmente al iniciar

    // Método para obtener la calificación promedio de una película(general)
    fun getAverageRating(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {

            _authToken.value?.let { token ->

                try {
                    _averageRatingState.value = AverageRatingState.Loading
                    val response =
                        apiServer.methods.getMovieAverageRating("Bearer $token", movieId)
                    if (response.isSuccessful) {
                        val averageRating = response.body()?.averageRating ?: 0.0
                        _averageRatingState.value = AverageRatingState.Success(averageRating)
                    } else {
                        _averageRatingState.value =
                            AverageRatingState.Error("Error: ${response.message()}")
                    }
                } catch (e: Exception) {
                    when (e) {
                        is HttpException -> {
                            Log.e("userCreateViewModel", "Error HTTP: ${e.message()}")
                            _averageRatingState.value =
                                AverageRatingState.Error("Error HTTP: ${e.message()}")
                        }

                        else -> {
                            Log.e("userCreateViewModel", "Error: ${e.message}")
                            _averageRatingState.value =
                                AverageRatingState.Error("Error: ${e.message}")
                        }
                    }
                }
            }
        }
    }

    fun getRatingForUser(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _authToken.value?.let { token ->
                try {
                    _averageRatingForUserState.value = AverageRatingForUserState.Loading
                    val response = apiServer.methods.getRatedForUser("Bearer $token", movieId)
                    if (response.isSuccessful) {
                        val userRating = response.body()?.userRating
                        if (userRating != null) {
                            _averageRatingForUserState.value = AverageRatingForUserState.Success(userRating.rating)
                        } else {
                            _averageRatingForUserState.value = AverageRatingForUserState.Error("No rating found for the specified movie.")
                        }
                    } else {
                        _averageRatingForUserState.value = AverageRatingForUserState.Error("Error: ${response.message()}")
                    }
                } catch (e: Exception) {
                    val errorMessage = when (e) {
                        is HttpException -> "Error HTTP: ${e.message}"
                        else -> "Error: ${e.message}"
                    }
                    Log.e("userCreateViewModel", errorMessage)
                    _averageRatingForUserState.value = AverageRatingForUserState.Error(errorMessage)
                }
            }
        }
    }



    // Función para resetear el estado a Ready
    fun setStateToReady1() {
        _averageRatingState.value = AverageRatingState.Ready
    }

    fun TopMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            _authToken.value?.let { token ->

                try {
                    _topMoviesState.value = TopMoviestState.Loading
                    val response = apiServer.methods.getTopRatedMovies("Bearer $token")
                    if (response.isSuccessful) {
                        val data = response.body()
                        val uniqueMovies =
                            data?.topRatedMovies?.distinctBy { it.id } // Filtra duplicados por ID
                        _topMoviesState.value = uniqueMovies?.let {
                            TopMoviestState.Success(
                                topMoviesResponse(it)
                            )
                        }
                            ?: TopMoviestState.Error("Error: Películas no encontradas")
                    } else {
                        _topMoviesState.value =
                            TopMoviestState.Error("Error: ${response.message()}")
                    }
                } catch (e: Exception) {
                    when (e) {
                        is HttpException -> {
                            Log.e("UserCreateViewModel", "Error HTTP: ${e.message()}")
                            _topMoviesState.value =
                                TopMoviestState.Error("Error HTTP: ${e.message()}")
                        }

                        else -> {
                            Log.e("UserCreateViewModel", "Error: ${e.message}")
                            _topMoviesState.value = TopMoviestState.Error("Error: ${e.message}")
                        }
                    }
                }
            }
        }
    }


    //agregar a witchList
    // Método para añadir a la lista de deseos
    fun addToWishlist(witchListData: witchListData) {
        _authToken.value?.let { token ->

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiServer.methods.addToWishlist("Bearer $token", witchListData)
                if (response.isSuccessful) {
                    //  _wishListGetState.value = WishlistGetState.Success("success") // Puedes actualizar el estado según tu lógica de UI
                }
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        _wishListGetState.value =
                            WishlistGetState.Error("Error HTTP: ${e.message()}")
                    }

                    else -> {
                        _wishListGetState.value = WishlistGetState.Error("Error: ${e.message}")
                    }
                }
            }
        }
        }
    }


    // Método para obtener la lista de deseos
    fun getWishlist() {
        viewModelScope.launch(Dispatchers.IO) {
            _authToken.value?.let { token ->

                try {
                    _wishlistState.value = WishlistGetState.Loading
                    val response = apiServer.methods.getWishlist("Bearer $token")
                    if (response.isSuccessful) {
                        val wishlistItems = response.body()
                        wishlistItems?.let {
                            _wishlistState.value = WishlistGetState.Success(it)
                        } ?: run {
                            _wishlistState.value = WishlistGetState.Error("Lista de deseos vacía")
                        }
                    } else {
                        _wishlistState.value =
                            WishlistGetState.Error("Error: ${response.message()}")
                    }
                } catch (e: Exception) {
                    when (e) {
                        is HttpException -> {
                            _wishlistState.value =
                                WishlistGetState.Error("Error HTTP: ${e.message()}")
                        }

                        else -> {
                            _wishlistState.value = WishlistGetState.Error("Error: ${e.message}")
                        }
                    }
                }
            }
        }
    }


//calificadas por usuario individual
    fun getMoviesReated() {
        viewModelScope.launch(Dispatchers.IO) {
            _authToken.value?.let { token ->

            try {
                _moviesReated.value = MoviesReated.Loading
                val response = apiServer.methods.getRatedMovies("Bearer $token")
                if (response.isSuccessful) {
                    val moviesReatedItems = response.body()
                    moviesReatedItems?.let {
                        _moviesReated.value = MoviesReated.Success(it)
                    } ?: run {
                        _moviesReated.value = MoviesReated.Error("Lista de Calificados vacía")

                    }
                } else {
                    _moviesReated.value = MoviesReated.Error("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        _moviesReated.value = MoviesReated.Error("Error HTTP: ${e.message()}")
                    }
                    else -> {
                        _moviesReated.value = MoviesReated.Error("Error: ${e.message}")
                    }
                }
            }
                }
        }
    }

    fun resetDeleteCommentState() {
        _deleteCommentState.value = DeleteCommentState.Ready
    }


    // Eliminar comentario principal
    fun deleteComment(commentId: String) {
        _authToken.value?.let { token ->

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _deleteCommentState.value = DeleteCommentState.Loading
                val response = apiServer.methods.deleteComment("Bearer $token", commentId)
                if (response.isSuccessful) {
                    _deleteCommentState.value =
                        DeleteCommentState.Success("Comment deleted successfully")
                } else {
                    _deleteCommentState.value =
                        DeleteCommentState.Error("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                _deleteCommentState.value = DeleteCommentState.Error(e.message ?: "Unknown error")
            }
        }
        }
    }

    // Eliminar respuesta específica
    fun deleteReply(replyId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _authToken.value?.let { token ->

                try {
                    _deleteReplyState.value = DeleteReplyState.Loading
                    val response = apiServer.methods.deleteReply("Bearer $token", replyId)
                    if (response.isSuccessful) {
                        _deleteReplyState.value =
                            DeleteReplyState.Success("Reply deleted successfully")
                    } else {
                        _deleteReplyState.value =
                            DeleteReplyState.Error("Error: ${response.message()}")
                    }
                } catch (e: Exception) {
                    _deleteReplyState.value = DeleteReplyState.Error(e.message ?: "Unknown error")
                }
            }
        }
    }

    fun createMovie(createMovieData: createMovieData){

        viewModelScope.launch(Dispatchers.IO) {
            _authToken.value?.let { token ->

                try {
                    val response =
                        apiServer.methods.createMovie("Bearer $token", createMovieData)
                    Log.i("userCreateViewModel", response.toString())


                } catch (e: Exception) {
                    when (e) {
                        is HttpException -> {
                            Log.i("userCreateViewModel", e.message())
                        }

                        else -> {
                            Log.i("userCreateViewModel", e.toString())

                        }
                    }
                }
            }
        }
    }

    /*fun updateMovie(movieId: Int, updatedMovieData: createMovieData) {
        viewModelScope.launch(Dispatchers.IO) {
            _authToken.value?.let { token ->
                try {
                    val response = apiServer.methods.updateMovie("Bearer $token", movieId, updatedMovieData)
                    Log.i("userUpdateViewModel", response.toString())
                } catch (e: Exception) {
                    when (e) {
                        is HttpException -> {
                            Log.i("userUpdateViewModel", e.message())
                        }
                        else -> {
                            Log.i("userUpdateViewModel", e.toString())
                        }
                    }
                }
            }
        }
    }*/



    fun getMovieCreate() {
        viewModelScope.launch(Dispatchers.IO) {
            _authToken.value?.let { token ->

                try {
                    _getMovieCreate.value = GetMovieCreate.Loading
                    val response = apiServer.methods.getMovieCreate("Bearer $token")
                    if (response.isSuccessful) {
                        val userData = response.body()
                        _getMovieCreate.value = userData?.let { GetMovieCreate.Success(it) }
                            ?: GetMovieCreate.Error("Error: Datos de la pelicula no encontrado")
                    } else {
                        _getMovieCreate.value = GetMovieCreate.Error("Error: ${response.message()}")
                    }
                } catch (e: Exception) {
                    when (e) {
                        is HttpException -> {
                            Log.i("datos:", e.message())
                            _getMovieCreate.value = GetMovieCreate.Error(e.message())
                        }

                    }
                }
            }
        }
    }


    // Método para buscar actores por nombre
    fun searchActorsByName(actorName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _authToken.value?.let { token ->

                try {
                    _searchActorsState.value = SearchActorsState.Loading

                    // Llamar al método de la API para buscar actores por nombre
                    val response =
                        apiServer.methods.searchActorsByName("Bearer $token", actorName)

                    if (response.isSuccessful) {
                        val actorsResponse = response.body()
                        actorsResponse?.let {
                            _searchActorsState.value = SearchActorsState.Success(it)
                        } ?: run {
                            _searchActorsState.value =
                                SearchActorsState.Error("Respuesta nula del servidor")
                        }
                    } else {
                        _searchActorsState.value =
                            SearchActorsState.Error("Error ${response.code()}: ${response.message()}")
                    }
                } catch (e: Exception) {
                    _searchActorsState.value = SearchActorsState.Error("Error: ${e.message}")
                }
            }
        }
    }

    fun addActor(actor: Actor) {
        _selectedActors.add(actor)
    }

    fun clearSelectedActors() {
        _selectedActors.clear()
    }
    // Método para limpiar el estado de búsqueda de actores
    fun clearSearchActorsState() {
        _searchActorsState.value = SearchActorsState.Ready
    }








    private val _deleteMovieState = MutableStateFlow<DeleteMovieState>(DeleteMovieState.Ready)
    val deleteMovieState: StateFlow<DeleteMovieState> = _deleteMovieState

    fun deleteMovie(movieId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _authToken.value?.let { token ->
                try {
                    _deleteMovieState.value = DeleteMovieState.Loading
                    val response = apiServer.methods.deleteMovie("Bearer $token", movieId)
                    if (response.isSuccessful) {
                        _deleteMovieState.value = DeleteMovieState.Success
                    } else {
                        val errorBody = response.errorBody()?.string()
                        _deleteMovieState.value = DeleteMovieState.Error(
                            "Error ${response.code()}: ${response.message()} - $errorBody"
                        )
                    }
                } catch (e: Exception) {
                    when (e) {
                        is HttpException -> {
                            Log.e("deleteMovie", "HttpException: ${e.message()}")
                            _deleteMovieState.value = DeleteMovieState.Error("HttpException: ${e.message()}")
                        }
                        else -> {
                            Log.e("deleteMovie", "Exception: ${e.message}", e)
                            _deleteMovieState.value = DeleteMovieState.Error("Exception: ${e.message}")
                        }
                    }
                }
            } ?: run {
                _deleteMovieState.value = DeleteMovieState.Error("Authentication token not available")
            }
        }
    }



    fun resetDeleteMovieState() {
        _deleteMovieState.value = DeleteMovieState.Ready
    }

    fun getMovieCreateById(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _authToken.value?.let { token ->

                try {
                    _getMovieCreateState.value = GetMovieCreateState.Loading
                    val response = apiServer.methods.getMovieCreateById("Bearer $token", movieId)
                    if (response.isSuccessful) {
                        response.body()?.let { movie ->
                            _getMovieCreateState.value = GetMovieCreateState.Success(movie)
                        } ?: run {
                            _getMovieCreateState.value = GetMovieCreateState.Error("Movie not found")
                        }
                    }else {
                        _getMovieCreateState.value = GetMovieCreateState.Error("Error: ${response.message()}")
                    }
                } catch (e: Exception) {
                    when (e) {
                        is HttpException -> {
                            Log.i("datos:", e.message())
                            _getMovieCreateState.value =GetMovieCreateState.Error(e.message())
                        }

                    }
                }
            }
        }
    }


}

sealed class UiState2 {
    data object Loading : UiState2()
    data object Ready : UiState2()
    data class Success (val msg : String) : UiState2()
    data class Error (val msg : String) : UiState2()
}


sealed class UiState {
    data object Loading : UiState()
    data object Ready : UiState()
    data class Success(val token: String): UiState()
    data class Error (val msg : String) : UiState()
}



sealed class UserDataState {
    data object Loading : UserDataState()
    data object Ready : UserDataState()
    data class Success(val userData: homeUserResponse) : UserDataState()
    data class Error(val msg: String) : UserDataState()
}



sealed class RecentMoviestState {
    data object Loading : RecentMoviestState()
    data object Ready : RecentMoviestState()
    data class Success(val data: recentMoviesResponse) : RecentMoviestState()
    data class Error(val msg: String) : RecentMoviestState()
}

sealed class MostViewsMoviestState {
    data object Loading : MostViewsMoviestState()
    data object Ready : MostViewsMoviestState()
    data class Success(val data: mostViewMoviesResponse) : MostViewsMoviestState()
    data class Error(val msg: String) : MostViewsMoviestState()
}
sealed class MovieState {
    object Loading : MovieState()
    object Ready :  MovieState()

    data class Success(val data: moviesResponse) : MovieState()
    data class Error(val msg: String) : MovieState()
}


sealed class CommentPostState {
    object Loading : CommentPostState()
    object Ready : CommentPostState()
    data class Success(val message: String) : CommentPostState()
    data class Error(val message: String) : CommentPostState()
}

sealed class EditCommentState {
    object Loading : EditCommentState()
    object Ready : EditCommentState()
    data class Success(val message: String) : EditCommentState()
    data class Error(val errorMessage: String) : EditCommentState()
}


// Selladas para manejar los estados de la lista de comentarios
sealed class CommentListState {
    object Loading : CommentListState()
    object Ready : CommentListState()
    data class Success(val comments: List<getCommentResponse>) : CommentListState()
    data class Error(val message: String) : CommentListState()
}

sealed class RepliesToCommentState {
    object Loading : RepliesToCommentState()
    object Ready : RepliesToCommentState()
    data class Success(val replies: List<ReplyComment>) : RepliesToCommentState()
    data class Error(val message: String) : RepliesToCommentState()
}


sealed class NotificationState {
    object Loading : NotificationState()
    object Ready : NotificationState()
    data class Success(val notifications: List<NotificationResponse>) : NotificationState()
    data class Error(val message: String) : NotificationState()
}

sealed class MarkNotificationState {
    object Loading : MarkNotificationState()
    object Ready : MarkNotificationState()
    data class Success(val message: String) : MarkNotificationState()
    data class Error(val message: String) : MarkNotificationState()
}


sealed class RateMovieState {
    object Loading : RateMovieState()
    object Ready : RateMovieState()
    data class Success(val message: String) : RateMovieState()
    data class Error(val message: String) : RateMovieState()
}

sealed class AverageRatingState {
    object Loading : AverageRatingState()
    object Ready : AverageRatingState()
    data class Success(val averageRating: Double) : AverageRatingState()
    data class Error(val message: String) : AverageRatingState()
}
sealed class AverageRatingForUserState {
    object Loading : AverageRatingForUserState()
    object Ready : AverageRatingForUserState()
    data class Success(val rating: Int) : AverageRatingForUserState()
    data class Error(val message: String) : AverageRatingForUserState()
}


sealed class SearchState {
    object Loading : SearchState()
    object Ready : SearchState()
    data class Success(val data: searchMoviesResponse?) : SearchState()
    data class Error(val message: String, val imageRes: Int) : SearchState()
}


sealed class TopMoviestState {
    data object Loading : TopMoviestState()
    data object Ready : TopMoviestState()
    data class Success(val data: topMoviesResponse) : TopMoviestState()
    data class Error(val msg: String) : TopMoviestState()
}



sealed class WishlistPostState {
    object Loading : WishlistPostState()
    object Ready : WishlistPostState()
    data class Success(val message: String) : WishlistPostState()
    data class Error(val errorMessage: String) : WishlistPostState()
}

sealed class WishlistGetState {
    object Loading :  WishlistGetState()
    object Ready :  WishlistGetState()
    data class Success(val data: wishListResponse) :  WishlistGetState()
    data class Error(val errorMessage: String) :  WishlistGetState()
}

sealed class MoviesReated {
    object Loading :  MoviesReated()
    object Ready :  MoviesReated()
    data class Success(val data: rateMoveResponse) :  MoviesReated()
    data class Error(val errorMessage: String) :  MoviesReated()
}


sealed class DeleteCommentState {
    object Loading : DeleteCommentState()
    object Ready : DeleteCommentState()
    data class Success(val message: String) : DeleteCommentState()
    data class Error(val errorMessage: String) : DeleteCommentState()
}

sealed class DeleteReplyState {
    object Loading : DeleteReplyState()
    object Ready : DeleteReplyState()
    data class Success(val message: String) : DeleteReplyState()
    data class Error(val errorMessage: String) : DeleteReplyState()
}


//admin
sealed class CreateMovie {
    data object Loading : CreateMovie()
    data object Ready : CreateMovie()
    data class Success (val data:createMovieData) : CreateMovie()
    data class Error (val msg : String) : CreateMovie()
}


sealed class GetMovieCreate {
    object Loading :  GetMovieCreate()
    object Ready :  GetMovieCreate()
    data class Success(val data: movieResponseAdminResponse) :  GetMovieCreate()
    data class Error(val errorMessage: String) :  GetMovieCreate()
}

sealed class SearchActorsState {
    object Loading : SearchActorsState()
    object Ready : SearchActorsState()
    data class Success(val actors: actorNameResponse) : SearchActorsState()
    data class Error(val errorMessage: String) : SearchActorsState()
}




sealed class LogoutResult {
    object Loading : LogoutResult()
    object Success : LogoutResult()
    data class Error(val message: String) : LogoutResult()
}


sealed class DeleteMovieState {
    object Loading : DeleteMovieState()
    object Ready : DeleteMovieState()
    object Success : DeleteMovieState()
    data class Error(val errorMessage: String) : DeleteMovieState()
}
sealed class UserRatingState {
    object Ready : UserRatingState()
    data class Success(val userRating: Int) : UserRatingState()
    data class Error(val message: String) : UserRatingState()
}



sealed class GetMovieCreateState {
    object Loading : GetMovieCreateState()
    object Ready :  GetMovieCreateState()

    data class Success(val data: MovieAdmin) : GetMovieCreateState()
    data class Error(val msg: String) : GetMovieCreateState()
}
