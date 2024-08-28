package com.example.fontendpicshare

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fontendlocket.GroupScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Định nghĩa NavHost với các màn hình (composables)
    NavHost(navController = navController, startDestination = "firstForm") {
        composable("firstForm") { FirstForm(navController) }  // Màn hình đầu tiên
        composable("login") { LoginScreen(navController) }  // Màn hình đăng nhập
        composable("registerForm") { RegisterScreen(navController) }  // Màn hình đăng ký
        composable("mainForm") { MainForm( navController) } // Màn hình chính sau khi đăng nhập
        composable("settingUser") {SettingUser(navController) }  // Màn hình cài đặt người dùng
        composable("reportMail") { ReportMail(navController) }  // Màn hình báo cáo qua email
        composable("changePassword") { ChangePassword(navController) }  // Màn hình thay đổi mật khẩu
        composable("groupScreen") { GroupScreen(navController)}
        composable("chatroomScreen") { ChatRoomScreen(navController)}

    }
}
