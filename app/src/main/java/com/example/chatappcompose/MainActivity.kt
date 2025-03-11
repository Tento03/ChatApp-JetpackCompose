package com.example.chatappcompose

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chatappcompose.auth.LoginScreen
import com.example.chatappcompose.auth.RegisterScreen
import com.example.chatappcompose.model.FcmTokenRequest
import com.example.chatappcompose.ui.theme.ChatAppComposeTheme
import com.example.chatappcompose.uiux.ChatScreen
import com.example.chatappcompose.uiux.UserScreen
import com.example.chatappcompose.api.UserApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChatAppComposeTheme {
                val navController= rememberNavController()
                val context= LocalContext.current
                val isLogin= isLoginStatus(context)
                NavHost(navController=navController, startDestination = if (isLogin) "User" else "Login"){
                    composable("Login"){
                        LoginScreen(navController)
                    }
                    composable("Register"){
                        RegisterScreen(navController)
                    }
                    composable("User"){
                        UserScreen(navController)
                        getToken()
                    }
                    composable(route = "Chat/{user}",
                        arguments = listOf(navArgument("user",{
                            type= NavType.StringType
                        }))
                    ){
                        val idReceiver=it.arguments?.getString("user")
                        if (idReceiver != null) {
                            ChatScreen(navController,idReceiver)
                        }
                    }
                }
//                UserScreen(navController)
            }
        }
    }
}

fun isLoginStatus(context: Context):Boolean{
    val sharedPreferences=context.getSharedPreferences("Login_Pref",Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean("isLoggedIn",false)
}

fun getToken(){
    FirebaseMessaging.getInstance().token.addOnCompleteListener(){
        if (!it.isSuccessful) {
            Log.d("FCM","Failed to Fetched FCM Token")
        }
        val token=it.result
        Log.d("FCM","Token is $token")
        registerToken(token)
    }
}

fun registerToken(token: String) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val userId=FirebaseAuth.getInstance().currentUser?.uid
            val fcmTokenRequest= FcmTokenRequest(userId!!, token)
            val response= UserApi.apiService.register_token(fcmTokenRequest)
            if (response.isSuccessful){
                Log.d("FCM","Register Token Succeed")
            }
            else{
                Log.d("FCM","Failed to register token")
            }
        }
        catch (e:Exception){
            Log.w("FCM", e.message.toString())
        }
    }
}