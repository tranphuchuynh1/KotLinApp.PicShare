package com.example.fontendpicshare

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.fontendlocket.ui.theme.FontEndLocKetTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {

    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Handle result here
            }
        }

        setContent {
            FontEndLocKetTheme {
                AppNavigation()
            }
        }
    }
}
