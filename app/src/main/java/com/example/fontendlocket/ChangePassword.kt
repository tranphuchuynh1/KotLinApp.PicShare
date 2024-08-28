package com.example.fontendpicshare

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePassword(navController: NavHostController) {
    // Các state quản lý dữ liệu nhập và thông báo
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    // Mật khẩu hiện tại được giả định là "204"
    val currentPassword = "204"

    // Bố cục chính
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFEFEFEF)).padding(16.dp)
    ) {
        // Nút quay lại
        Button(
            onClick = { navController.popBackStack() },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726)),
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Text(text = "Back", color = Color.Black)             // Văn bản của nút quay lại
        }

        // Form đổi mật khẩu
        Column(
            modifier = Modifier.fillMaxWidth().align(Alignment.Center).padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tiêu đề form
            Text(
                text = "CHANGE PASSWORD",                        // Nội dung tiêu đề
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),
                modifier = Modifier.padding(bottom = 24.dp),
                textAlign = TextAlign.Center
            )

            // Trường nhập mật khẩu cũ
            OutlinedTextField(
                value = oldPassword,
                onValueChange = { oldPassword = it },
                label = { Text("Mật khẩu cũ") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(), // Biến đổi ký tự thành dạng mật khẩu
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFFFFA726),
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color(0xFFFFA726)
                )
            )

            // Trường nhập mật khẩu mới
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("Mật khẩu mới") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFFFFA726),
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color(0xFFFFA726)
                )
            )

            // Trường nhập xác nhận mật khẩu mới
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Xác nhận mật khẩu mới") },
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

            // Hiển thị thông báo lỗi nếu có
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Hiển thị thông báo thành công nếu có
            if (successMessage.isNotEmpty()) {
                Text(
                    text = successMessage,
                    color = Color.Green,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Nút Đổi mật khẩu
            Button(
                onClick = {
                    // Kiểm tra nếu tất cả các trường được điền
                    if (oldPassword.isNotEmpty() && newPassword.isNotEmpty() && confirmPassword.isNotEmpty()) {
                        // Kiểm tra mật khẩu cũ có đúng không
                        if (oldPassword == currentPassword) {
                            // Kiểm tra mật khẩu mới có khớp với mật khẩu xác nhận không
                            if (newPassword == confirmPassword) {
                                // Kiểm tra nếu mật khẩu mới không giống với mật khẩu cũ
                                if (newPassword != oldPassword) {
                                    // Thành công
                                    successMessage = "Đổi mật khẩu thành công!"
                                    errorMessage = ""
                                    navController.popBackStack()  // Quay lại màn hình trước
                                } else {
                                    errorMessage = "Mật khẩu mới không được giống mật khẩu cũ"
                                    successMessage = ""
                                }
                            } else {
                                errorMessage = "Mật khẩu xác nhận không trùng khớp"
                                successMessage = ""
                            }
                        } else {
                            errorMessage = "Mật khẩu cũ không chính xác"
                            successMessage = ""
                        }
                    } else {
                        errorMessage = "Vui lòng điền đầy đủ thông tin"
                        successMessage = ""
                    }
                },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726)), // Màu nền nút
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(text = "Đổi mật khẩu", fontSize = 18.sp, color = Color.Black) // Văn bản nút
            }
        }
    }
}
