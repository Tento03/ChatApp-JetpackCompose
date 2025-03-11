package com.example.chatappcompose.uiux

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.Uri
import coil3.compose.AsyncImage
import com.example.chatappcompose.R
import com.example.chatappcompose.model.User
import com.example.chatappcompose.ui.theme.ChatAppComposeTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson

@Composable
fun UserScreen(navController: NavController, modifier: Modifier = Modifier) {
    val firebaseDatabase = FirebaseDatabase.getInstance().getReference("user")
    val userList = remember { mutableStateListOf<User>() }

    val firebaseAuth = FirebaseAuth.getInstance()
    val idSender = firebaseAuth.currentUser?.uid

    if (idSender != null) {
        firebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    userList.clear()
                    for (i in snapshot.children) {
                        val user = i.getValue(User::class.java)
                        if (user != null && user.id != idSender) {
                            userList.add(user)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    Row(modifier.padding(top = 20.dp), horizontalArrangement = Arrangement.End) {
        TextButton(onClick = {
            firebaseAuth.signOut()
            navController.navigate("Login")
        }
        ) {
            Text("Logout")
        }
    }

    LazyColumn(
        modifier
            .padding(8.dp, top = 100.dp)
            .fillMaxWidth()
    ) {
        items(userList) { user ->
            UserCard(
                user,
                setOnClick = { selectedUser ->
                    val userJson = Gson().toJson(selectedUser)
                    val userJsonUri = android.net.Uri.encode(userJson)
                    navController.navigate("Chat/$userJsonUri")
                },
                modifier = modifier
            )
        }
    }
}


@Composable
fun UserCard(user: User,setOnClick : (user:User)->Unit,modifier: Modifier = Modifier) {
    Card(modifier
        .padding(8.dp)
        .fillMaxWidth()
        .background(color = Color.LightGray)
        .clickable {
            setOnClick(user)
        }
    ) {
        Column(modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AsyncImage(
                    model = user.image, contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier=Modifier.size(64.dp).clip(shape = CircleShape)
                )
                Text(user.username, textAlign = TextAlign.Center, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//private fun UserCardPreview() {
//    ChatAppComposeTheme {
//        UserCard(user = User("","","","","",""))
//    }
//}