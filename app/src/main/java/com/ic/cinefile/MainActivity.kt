package com.ic.cinefile


import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.ic.cinefile.Navigation.AppNavigation
import com.ic.cinefile.Navigation.screenRoute
//import androidx.navigation.compose.rememberNavController
import com.ic.cinefile.screens.HomeAppScreen
import com.ic.cinefile.ui.theme.CineFileTheme
import com.ic.cinefile.viewModel.UserCreateViewModelFactory
import com.ic.cinefile.viewModel.userCreateViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: userCreateViewModel by viewModels {
        UserCreateViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CheckAuthState(viewModel)
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}

@Composable
fun CheckAuthState(viewModel: userCreateViewModel) {
    val authTokenState = viewModel.authToken.collectAsState(initial = null)
    val authToken = authTokenState.value
    val navController = rememberNavController()

    LaunchedEffect(authToken) {
        if (authToken != null) {
            viewModel.fetchUserData()
            val userRole = viewModel.getUserRole()
            if (userRole == "admin") {
                navController.navigate(screenRoute.HomeAdmin.route) {
                    popUpTo(0)
                }
            } else {
                navController.navigate(screenRoute.HomeAdmin.route) {
                    popUpTo(0)
                }
            }
        } else {
            navController.navigate(screenRoute.Login.route) {
                popUpTo(0)
            }
        }
    }

    AppNavigation(viewModel, navController)
}




