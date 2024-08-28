package com.example.fontendpicshare

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun ChatRoomScreen(navController: NavHostController) {
    var messageText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<Pair<String, Boolean>>() } // Pair<String, Boolean>: Boolean indicates if it's sent by user

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header with back button, avatar, name, and info icon
        HeaderWithBackButtonAndInfoIcon(
            userName = "User",
            onBackButtonClick = { navController.popBackStack() }, // Navigate back
            onInfoIconClick = { /* Handle info icon click here */ }
        )

        // Message list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true // Make the latest message appear at the bottom
        ) {
            items(messages.size) { index ->
                val (message, isUser) = messages[index]
                MessageBubble(message, isUser)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // Input field and send button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier
                    .weight(1f)
                    .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                    .padding(12.dp),
                textStyle = TextStyle(fontSize = 16.sp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (messageText.isNotEmpty()) {
                        messages.add(messageText to true) // Add user message
                        messageText = ""

                        // Simulate a response
                        messages.add("Mình tên Mều Bếu.." to false)
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "SEND")
            }
        }
    }
}

@Composable
fun HeaderWithBackButtonAndInfoIcon(
    userName: String,
    onBackButtonClick: () -> Unit,
    onInfoIconClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Back button
        IconButton(onClick = onBackButtonClick) {
            Icon(
                painter = painterResource(id = R.drawable.back), // Replace with your back icon resource
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        // User name and avatar
        Row(
            modifier = Modifier.weight(1f) // Đặt thuộc tính weight cho Row chứa ảnh đại diện và tên người dùng để nó chiếm hết không gian còn lại giữa icon quay lại và icon thông tin
            .padding(start = 16.dp), // css khoảng cách giua~ AnhDaiDien-TenUser va` Icon Back
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start // căn chỉnh các element ve ben trai'
        ) {
            // Circular avatar
            Image(
                painter = painterResource(id = R.drawable.user), // Replace with your user avatar resource
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(8.dp)) // khoảng cách giữa ảnh đại diện và tên user

            // User name
            Text(
                text = userName,
                style = TextStyle(fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
            )
        }

        // Info icon
        IconButton(onClick = onInfoIconClick) {
            Icon(
                painter = painterResource(id = R.drawable.i), //icon chi tiet'
                contentDescription = "Info",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun MessageBubble(message: String, isUser: Boolean) {
    val backgroundColor = if (isUser) Color(0xFF6200EE) else Color.LightGray
    val textColor = if (isUser) Color.White else Color.Black
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        contentAlignment = alignment
    ) {
        Text(
            text = message,
            modifier = Modifier
                .background(backgroundColor, shape = MaterialTheme.shapes.medium)
                .padding(12.dp),
            color = textColor,
            style = TextStyle(fontSize = 16.sp)
        )
    }
}
