package com.example.ssd_ai_playground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ssd_ai_playground.ui.navigation.TodoNavHost
import com.example.ssd_ai_playground.ui.theme.SSD_AI_PlaygroundTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SSD_AI_PlaygroundTheme {
                TodoNavHost()
            }
        }
    }
}
