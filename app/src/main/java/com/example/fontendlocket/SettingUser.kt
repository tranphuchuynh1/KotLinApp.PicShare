package com.example.fontendpicshare

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingUser(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val userName = sharedPreferences.getString("user_name", "Tên người dùng") ?: "Tên người dùng"
    val userProfileImageUri = sharedPreferences.getString("user_profile_image", null)

    var profileImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val storage = FirebaseStorage.getInstance()

    // Set up Activity Result API for picking images
    val getContent = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = uri
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            profileImageBitmap = bitmap

            // Upload image to Firebase Storage
            uploadImageToFirebase(context, uri)
        }
    }

    LaunchedEffect(userProfileImageUri) {
        userProfileImageUri?.let {
            val storageRef = storage.getReferenceFromUrl(it)
            try {
                val bytes = storageRef.getBytes(Long.MAX_VALUE).await()
                profileImageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (e: Exception) {
                e.printStackTrace() // Handle error
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)) // Dark background color
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // User profile image
            val defaultProfileBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.user)
            androidx.compose.foundation.Image(
                bitmap = (profileImageBitmap ?: defaultProfileBitmap).asImageBitmap(),
                contentDescription = "User Profile Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // User name
            Text(
                text = userName,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Button to pick an image
            Button(onClick = { getContent.launch("image/*") }) {
                Text("Update Profile Image")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Other settings
            SettingsSection("Thiết lập Tiện ích", icon = R.drawable.maintenance) {
                SettingItem("Mời tham gia qua link", R.drawable.share)
                SettingItem("Phản hồi", R.drawable.email) {
                    navController.navigate("reportMail")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            SettingsSection("Xã Hội", icon = R.drawable.social) {
                SettingItem("Bạn Bè", R.drawable.group) {
                    navController.navigate("groupScreen")
                }
                SettingItem("Chup Ảnh", R.drawable.logo) {
                    navController.navigate("mainForm")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            SettingsSection("Tổng quát", icon = R.drawable.checklist) {
                SettingItem("Thay đổi mật khẩu", R.drawable.cycle) {
                    navController.navigate("changePassword")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            SettingsSection("Vùng nguy hiểm", icon = R.drawable.warning) {
                SettingItem("Đăng xuất", R.drawable.logout) {
                    showLogoutDialog = true // Show logout confirmation dialog
                }
                SettingItem("Xóa tài khoản", R.drawable.bin)
            }
        }

        // Logout confirmation dialog
        if (showLogoutDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)), // Dim background
                contentAlignment = Alignment.BottomCenter // Position dialog at bottom
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF121212))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Bạn có chắc chắn muốn đăng xuất không?",
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Divider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)

                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                            navController.navigate("firstForm")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Đăng Xuất",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    Divider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)

                    TextButton(
                        onClick = { showLogoutDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Hủy",
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, icon: Int? = null, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            if (icon != null) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp)
                )
            }
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        content() // Invoke composable lambda provided
    }
}

@Composable
fun SettingItem(text: String, icon: Int, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1F1F1F))
            .padding(16.dp)
            .clickable(onClick = onClick), // Make clickable
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, color = Color.White, fontSize = 16.sp)
    }
}

private fun uploadImageToFirebase(context: Context, uri: Uri) {
    val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/${uri.lastPathSegment}")
    val uploadTask = storageRef.putFile(uri)

    uploadTask.addOnSuccessListener {
        storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
            val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putString("user_profile_image", downloadUrl.toString())
                apply()
            }
        }
    }.addOnFailureListener {
        it.printStackTrace() // Handle error
    }
}
