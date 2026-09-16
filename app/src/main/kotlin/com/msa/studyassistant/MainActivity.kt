package com.msa.studyassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.msa.studyassistant.di.AppContainer
import com.msa.studyassistant.ui.navigation.AppRoot

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as StudyAssistantApplication).container
        setContent {
            AppRoot(container)
        }
    }
}
