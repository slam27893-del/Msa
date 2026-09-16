package com.msa.studyassistant.di

import android.content.Context
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.msa.studyassistant.curriculum.CurriculumDataSource
import com.msa.studyassistant.curriculum.SampleCurriculum
import com.msa.studyassistant.model.CurriculumSubject
import com.msa.studyassistant.storage.KeyValueStore
import com.msa.studyassistant.storage.StudentStateRepository
import com.msa.studyassistant.data.SharedPreferencesKeyValueStore
import com.msa.studyassistant.data.ThemeStore
import com.msa.studyassistant.ui.MainViewModel

/**
 * حاوية الاعتماديات البسيطة (بدون مكتبة DI — تكفي للـ Prototype).
 * كل التبعيات في مكان واحد، ويسهل استبدالها لاحقًا بمكونات حقيقية
 * (مصدر منهج سحابي، قاعدة بيانات، حسابات مستخدمين...).
 */
class AppContainer(context: Context) {

    private val prefs = context.getSharedPreferences("msa_local_store", Context.MODE_PRIVATE)

    /** التخزين المحلي: SharedPreferences الآن، وDataStore/Room لاحقًا. */
    val keyValueStore: KeyValueStore = SharedPreferencesKeyValueStore(prefs)

    /** إعدادات المظهر (فاتح/داكن/تلقائي) — محفوظة محليًا. */
    val themeStore = ThemeStore(keyValueStore)

    /** حالة الطالب: تُحمَّل من التخزين وتُحفَظ مع كل تعديل. */
    val stateRepository = StudentStateRepository(keyValueStore)

    /** مصدر المنهج: بيانات تجريبية الآن، المناهج الرسمية لاحقًا. */
    val curriculumSource: CurriculumDataSource = SampleCurriculum

    val curriculum: List<CurriculumSubject> = curriculumSource.subjects

    val viewModelFactory = viewModelFactory {
        initializer { MainViewModel(stateRepository, curriculum) }
    }
}
