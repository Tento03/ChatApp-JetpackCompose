package com.example.chatappcompose.auth

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.chatappcompose.R
import com.example.chatappcompose.ui.theme.ChatAppComposeTheme
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(navController: NavController,modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var hasErroremail by remember { mutableStateOf(false) }
    var hasErrorPassword by remember { mutableStateOf(false) }
    val focusRequesteremail by remember { mutableStateOf(FocusRequester()) }
    val focusRequesterPassword by remember { mutableStateOf(FocusRequester()) }
    val keyboardController= LocalSoftwareKeyboardController.current
    val context= LocalContext.current

    Column(modifier
        .fillMaxSize()
        .paint(painterResource(R.drawable.bg_login), contentScale = ContentScale.FillBounds),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(modifier
            .padding(top = 100.dp, start = 20.dp, end = 20.dp)
            .height(200.dp)
            .width(280.dp)
            .paint(painterResource(R.drawable.bg_login), contentScale = ContentScale.FillBounds))
        Card(modifier
            .padding(start = 20.dp, end = 20.dp)
            .paint(painter = painterResource(R.drawable.bg_white), contentScale = ContentScale.FillBounds)
            .height(300.dp)
            .width(280.dp)
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email=it
                    hasErroremail=false
                },
                modifier.padding(top = 10.dp,start = 10.dp, end = 10.dp).focusRequester(focusRequesteremail),
                isError = hasErroremail,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email
                ),
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Person,null) }
            )
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password=it
                    hasErrorPassword=false
                },
                modifier.padding(top = 10.dp,start = 10.dp, end = 10.dp).focusRequester(focusRequesterPassword),
                isError = hasErrorPassword,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {keyboardController?.hide()}
                ),
                visualTransformation = PasswordVisualTransformation(),
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock,null) }
            )
            Button(onClick = {
                if (email.isEmpty()){
                    hasErroremail=true
                    focusRequesteremail.requestFocus()
                }
                else if (password.isEmpty()){
                    hasErrorPassword=true
                    focusRequesterPassword.requestFocus()
                }
                else{
                    val firebaseAuth=FirebaseAuth.getInstance()
                    firebaseAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener{
                            if (it.isSuccessful){
                                saveLoginStatus(context,true)
                                Toast.makeText(context,"Login Succesfully",Toast.LENGTH_SHORT).show()
                                navController.navigate("User")
                            }
                            else{
                                Toast.makeText(context,"Login Failed",Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            },modifier.padding(top = 20.dp, start = 10.dp, end = 10.dp).fillMaxWidth(), shape = RoundedCornerShape(0.dp), colors = ButtonDefaults.buttonColors(Color.Black)) {
                Text("Login", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            TextButton(onClick = {navController.navigate("Register")}) {
                Text("Dont Have An Account?", color = Color.Black)
            }
            if (hasErroremail){
                focusRequesteremail.requestFocus()
                Toast.makeText(context,"Please fill Error Field",Toast.LENGTH_SHORT).show()
            }
            else if (hasErrorPassword){
                focusRequesterPassword.requestFocus()
                Toast.makeText(context,"Please fill Error Field",Toast.LENGTH_SHORT).show()
            }
        }
    }
}

fun saveLoginStatus(context: Context,isLogin:Boolean){
    val sharedPreferences=context.getSharedPreferences("Login_Pref",Context.MODE_PRIVATE)
    val editor=sharedPreferences.edit()
    editor.putBoolean("isLoggedIn",isLogin)
    editor.apply()
}

@Preview
@Composable
private fun LoginPrev() {
    ChatAppComposeTheme {
        val navController= rememberNavController()
        LoginScreen(navController)
    }
}