package com.example.fontendpicshare

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFEFEF))
            .padding(16.dp)
    ) {
        Button(
            onClick = { navController.popBackStack() },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726)),
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Text(text = "Back", color = Color.Black)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "LOGIN",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),
                modifier = Modifier.padding(bottom = 24.dp),
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Tài khoản") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFFFFA726),
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color(0xFFFFA726)
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật khẩu") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFFFFA726),
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color(0xFFFFA726)
                )
            )

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val userId = auth.currentUser?.uid
                                    userId?.let { id ->
                                        db.collection("users").document(id).get()
                                            .addOnSuccessListener { document ->
                                                val userName = document.getString("name")
                                                userName?.let { name ->
                                                    // Lưu tên người dùng vào SharedPreferences
                                                    val sharedPreferences = navController.context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                                    sharedPreferences.edit().putString("user_name", name).apply()

                                                    // Chuyển đến mainForm
                                                    navController.navigate("mainForm") {
                                                        popUpTo("mainForm") { inclusive = true }
                                                    }
                                                } ?: run {
                                                    errorMessage = "Không tìm thấy tên người dùng."
                                                }
                                            }
                                            .addOnFailureListener { e ->
                                                errorMessage = "Lỗi khi lấy thông tin người dùng: ${e.localizedMessage}"
                                            }
                                    }
                                } else {
                                    errorMessage = "Đăng nhập thất bại: ${task.exception?.localizedMessage}"
                                }
                            }
                    } else {
                        errorMessage = "Email hoặc mật khẩu không được để trống"
                    }
                },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(text = "Đăng nhập", fontSize = 18.sp, color = Color.Black)
            }
        }
    }
}
