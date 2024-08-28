package com.example.fontendlocket

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.fontendpicshare.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupScreen(navController: NavHostController) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    var searchQuery by remember { mutableStateOf("") }
    var userResults by remember { mutableStateOf<List<User>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    // Hàm tìm kiếm người dùng trong Firestore
    fun searchUsers(query: String) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                firestore.collection("users")
                    .whereGreaterThanOrEqualTo("email", query)
                    .whereLessThanOrEqualTo("email", query + '\uf8ff')
                    .get()
                    .addOnSuccessListener { result ->
                        userResults = result.mapNotNull { document ->
                            val email = document.getString("email")
                            val name = document.getString("name")
                            val profileImageUrl = document.getString("profileImageUrl")
                            User(email ?: "", name ?: "", profileImageUrl)
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Error fetching users", Toast.LENGTH_LONG).show()
                    }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        BasicTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray)
                .padding(16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 16.sp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { searchUsers(searchQuery) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Tìm kiếm")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(userResults) { user ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color.DarkGray)
                        .clickable {
                            // Xử lý khi nhấn vào người dùng
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hiển thị ảnh đại diện (dùng ảnh mặc định nếu không có)
                    androidx.compose.foundation.Image(
                        painter = if (user.profileImageUrl.isNullOrEmpty()) {
                            painterResource(id = R.drawable.user)
                        } else {
                            rememberAsyncImagePainter(user.profileImageUrl)
                        },
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Hiển thị tên và email của người dùng
                    Column {
                        Text(text = user.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = user.email, color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

// Lớp dữ liệu để chứa thông tin người dùng
data class User(val email: String, val name: String, val profileImageUrl: String?)
