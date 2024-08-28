package com.example.fontendpicshare

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun FirstForm(navController: NavHostController) {
    // Giao diện chính
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Hình ảnh chính
        Image(
            painter = painterResource(id = R.drawable.nen),
            contentDescription = "Phone Image",
            contentScale = ContentScale.Fit, // Giữ nguyên tỉ lệ hình ảnh
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp)
                .padding(bottom = 32.dp)
        )

        // Logo và Tên Ứng Dụng
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Locket Logo",
                modifier = Modifier.size(45.dp)
            )

            Text(
                text = "PicShare", // Tên ứng dụng
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFA726),
                modifier = Modifier.padding(start = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        // Mô tả ứng dụng
        Text(
            text = "Chia sẻ hình ảnh trực tiếp\ncủa cá nhân",  // Nội dung mô tả ứng dụng
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp // Khoảng cách giữa các dòng chữ
        )

        // Row chứa hai nút: Đăng Nhập và Đăng Ký
        Row(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nút Đăng Nhập
            Button(
                onClick = { navController.navigate("login") }, // Điều hướng đến màn hình đăng nhập
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726)), // Màu cam cho nút
                modifier = Modifier
                    .weight(1f)  // Chia đều trọng số giữa hai nút
                    .height(48.dp)
            ) {
                Text(
                    text = "Đăng Nhập",// Nội dung nút
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            // Nút Đăng Ký
            Button(
                onClick = { navController.navigate("registerForm") }, // Điều hướng đến màn hình đăng ký
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726)), // Màu cam cho nút
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = "Đăng Ký", // Nội dung nút
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
