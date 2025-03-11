package com.example.chatappcompose.auth

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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import com.example.chatappcompose.model.User
import com.example.chatappcompose.ui.theme.ChatAppComposeTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import java.security.MessageDigest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController,modifier: Modifier = Modifier) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var hasErroremail by remember { mutableStateOf(false) }
    var hasErrorPassword by remember { mutableStateOf(false) }
    var hasErrorUsername by remember { mutableStateOf(false) }
    var hasErrorGender by remember { mutableStateOf(false) }
    var focusRequesteremail by remember { mutableStateOf(FocusRequester()) }
    var focusRequesterPassword by remember { mutableStateOf(FocusRequester()) }
    var focusRequesterUsername by remember { mutableStateOf(FocusRequester()) }
    var focusRequesterGender by remember { mutableStateOf(FocusRequester()) }
    val keyboardController= LocalSoftwareKeyboardController.current
    val context= LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val genderList= listOf("Male","Female")
    var gender by remember { mutableStateOf("") }

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
            .height(420.dp)
            .width(280.dp)
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username=it
                    hasErrorUsername=false
                },
                modifier.padding(top = 10.dp,start = 10.dp, end = 10.dp).focusRequester(focusRequesterUsername),
                isError = hasErrorUsername,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                label = { Text("Username") },
                leadingIcon = { Icon(Icons.Default.Person,null) }
            )
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
                leadingIcon = { Icon(Icons.Default.Email,null) }
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
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded=!expanded
                },
                modifier.focusRequester(focusRequesterGender).padding(top = 20.dp, start = 10.dp, end = 10.dp),
            ) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = {},
                    modifier = Modifier.menuAnchor().fillMaxWidth().focusRequester(focusRequesterGender),
                    trailingIcon = {ExposedDropdownMenuDefaults.TrailingIcon(expanded)},
                    readOnly = true,
                    isError = hasErrorGender
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {expanded=false},
                ) {
                    genderList.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                gender=it
                                expanded=false
                            }
                        )
                    }
                }
            }
            Button(onClick = {
                if (username.isEmpty()){
                    hasErrorUsername=true
                    focusRequesterUsername.requestFocus()
                }
                else if (email.isEmpty()){
                    hasErroremail=true
                    focusRequesteremail.requestFocus()
                }
                else if (password.isEmpty()){
                    hasErrorPassword=true
                    focusRequesterPassword.requestFocus()
                }
                else if (gender.isEmpty()){
                    hasErrorGender=true
                    focusRequesterGender.requestFocus()
                }
                else if (username.isNotEmpty() && password.isNotEmpty() && email.isNotEmpty() && gender.isNotEmpty()){
                    val hashPassword= passwordhash(password)
                    val firebaseAuth=FirebaseAuth.getInstance()
                    val firebaseDatabase=FirebaseDatabase.getInstance().getReference("user")
                    firebaseAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener{
                            if (it.isSuccessful){
                                val id=FirebaseAuth.getInstance().currentUser?.uid
                                val image=""
                                val user=User(id!!,username,email, hashPassword,gender,image)
                                if (id != null) {
                                    firebaseDatabase.child(id).setValue(user)
                                }
                                Toast.makeText(context,"Register Succesfully",Toast.LENGTH_SHORT).show()
                                navController.navigate("Login")
                            }
                            else{
                                Toast.makeText(context,"Register Failed",Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            },modifier.padding(top = 20.dp, start = 10.dp, end = 10.dp).fillMaxWidth(), shape = RoundedCornerShape(0.dp), colors = ButtonDefaults.buttonColors(Color.Black)) {
                Text("Register", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            TextButton(onClick = {navController.navigate("Login")}) {
                Text("Have An Account?", color = Color.Black)
            }
            if (hasErrorUsername){
                focusRequesterUsername.requestFocus()
                Toast.makeText(context,"Please fill Username Field",Toast.LENGTH_SHORT).show()
            }
            else if (hasErroremail){
                focusRequesteremail.requestFocus()
                Toast.makeText(context,"Please fill Email Field",Toast.LENGTH_SHORT).show()
            }
            else if (hasErrorPassword){
                focusRequesterPassword.requestFocus()
                Toast.makeText(context,"Please fill Password Field",Toast.LENGTH_SHORT).show()
            }
            else if (hasErrorGender){
                focusRequesterGender.requestFocus()
                Toast.makeText(context,"Please fill Gender Field",Toast.LENGTH_SHORT).show()
            }
        }

    }
}

fun passwordhash(input:String):String{
    var byte=MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
    return byte.joinToString(""){
        "%02x".format(it)
    }
}

@Preview
@Composable
private fun RegisterPrev() {
    ChatAppComposeTheme {
        val navController= rememberNavController()
        RegisterScreen(navController)
    }
}