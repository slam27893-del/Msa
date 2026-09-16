package com.msa.studyassistant

import android.app.Application
import com.msa.studyassistant.di.AppContainer

class StudyAssistantApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
