package com.example.chatappcompose.uiux

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.chatappcompose.model.Chat
import com.example.chatappcompose.model.MessageRequest
import com.example.chatappcompose.model.User
import com.example.chatappcompose.api.UserApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChatScreen(navController: NavController,userJson:String,modifier: Modifier = Modifier) {
    val firebaseDatabase=FirebaseDatabase.getInstance().getReference("chat")
    val chatList = remember { mutableStateListOf<Chat>() }
    val firebaseAuth=FirebaseAuth.getInstance()
    val idSender=firebaseAuth.currentUser?.uid

    val user=Gson().fromJson(userJson,User::class.java)
    val idReceiver=user.id

    firebaseDatabase.orderByChild("time").addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                chatList.clear()
                for (chatSnapshot in snapshot.children) {
                    val chat = chatSnapshot.getValue(Chat::class.java)
                    if (chat != null &&
                        (chat.idSender == idSender && chat.idReceiver == idReceiver ||
                                chat.idSender == idReceiver && chat.idReceiver == idSender)) {
                        chatList.add(chat)
                    }
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {

        }
    })


    Scaffold(
        bottomBar = {
            ChatInputBar(idReceiver)
        },
        content = {
            Column(modifier
                .padding(8.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp))
            {
                Row(modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp))
                {
                    AsyncImage(model = user.image,
                        contentScale = ContentScale.FillBounds,
                        contentDescription = null)
                    Text(user.username)
                }
                LazyColumn {
                    items(chatList){
                        ChatMessage(it)
                    }
                }
            }
        }
    )
}

@Composable
fun ChatInputBar(idReceiver: String) {
    val firebaseDatabase = FirebaseDatabase.getInstance().getReference("chat")
    val firebaseAuth = FirebaseAuth.getInstance()

    val idSender = firebaseAuth.currentUser?.uid
    var message by remember { mutableStateOf("") }
    val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().time)
    val chatId = "chat $idSender $idReceiver $time "

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 50.dp)
    ) {
        OutlinedTextField(
            value = message,
            onValueChange = {
                message = it
            },
            placeholder = { Text("Type a message") },
            trailingIcon = {
                IconButton(onClick = {
                    try {
                        CoroutineScope(Dispatchers.IO).launch {
                    if (idSender != null) {
                        if (message.isNotEmpty() && idSender.isNotEmpty() && idReceiver.isNotEmpty()) {
                            val chat = Chat(message, idSender, idReceiver, time)
                            firebaseDatabase.child(chatId).setValue(chat)

                            val message=MessageRequest(idSender,idReceiver,message)
                            val response= UserApi.apiService.send_message(message)
                            if (response.isSuccessful){
                                Log.d("FCM","Pesan terkirim")
                            }
                            else{
                                Log.w("FCM","Pesan tidak terkirim")
                            }
                               }
                           }
                        }
                    }
                    catch (e:Exception){
                        e.printStackTrace()
                    }
                }) {
                    Icon(Icons.Filled.Send, contentDescription = null)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
fun ChatMessage(chat: Chat,modifier: Modifier = Modifier) {
    val firebaseAuth=FirebaseAuth.getInstance()
    val idSender=firebaseAuth.currentUser?.uid

    Row(modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = if (chat.idSender==idSender) Arrangement.End else Arrangement.Start
    ) {
        Box(modifier
            .background(if (chat.idSender==idSender) Color.Green else Color.Gray , shape = RoundedCornerShape(12.dp))
            .padding(12.dp)){
            Text(chat.message, color = Color.Black)
        }
    }
}
