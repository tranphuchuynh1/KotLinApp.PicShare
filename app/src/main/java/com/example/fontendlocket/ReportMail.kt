package com.example.fontendpicshare

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportMail(navController: NavHostController) {
    val context = LocalContext.current
    var userEmail by remember { mutableStateOf(TextFieldValue("")) }
    var message by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Báo cáo sự cố", color = Color.White, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // Trường nhập email của người dùng
        OutlinedTextField(
            value = userEmail,
            onValueChange = { userEmail = it },
            label = { Text("Địa chỉ email của bạn", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.White)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Trường nhập mô tả sự cố
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Hãy cho chúng tôi biết điều gì đang xảy ra...", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            textStyle = LocalTextStyle.current.copy(color = Color.White)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Nút gửi phản hồi
        Button(
            onClick = {
                if (userEmail.text.isBlank()) {
                    Toast.makeText(context, "Vui lòng nhập địa chỉ email của bạn", Toast.LENGTH_LONG).show()
                } else if (message.text.isBlank()) {
                    Toast.makeText(context, "Vui lòng mô tả sự cố của bạn", Toast.LENGTH_LONG).show()
                } else {
                    // Tạo Intent để gửi email với thông tin của người dùng
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:") // Chỉ định ứng dụng email
                        putExtra(Intent.EXTRA_EMAIL, arrayOf("tranphuchuynh1@gmail.com"))
                        putExtra(Intent.EXTRA_SUBJECT, "Báo cáo sự cố từ ứng dụng")
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Email người dùng: ${userEmail.text}\n\nMô tả sự cố:${message.text}"
                        )
                    }

                    context.startActivity(Intent.createChooser(emailIntent, "Gửi email bằng:"))
                    navController.popBackStack() // Quay lại màn hình trước đó sau khi gửi thành công
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
        ) {
            Text("Gửi", color = Color.Black, fontSize = 18.sp)
        }
    }
}
