@file:OptIn(ExperimentalMaterial3Api::class)

package com.msa.studyassistant.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.msa.studyassistant.R
import com.msa.studyassistant.di.AppContainer
import com.msa.studyassistant.ui.MainViewModel
import com.msa.studyassistant.ui.screens.HomeScreen
import com.msa.studyassistant.ui.screens.LessonScreen
import com.msa.studyassistant.ui.screens.MissedLessonsScreen
import com.msa.studyassistant.ui.screens.ProgressScreen
import com.msa.studyassistant.ui.screens.ScheduleScreen
import com.msa.studyassistant.ui.screens.SettingsScreen
import com.msa.studyassistant.ui.screens.SetupScreen
import com.msa.studyassistant.ui.screens.SubjectDetailScreen
import com.msa.studyassistant.ui.screens.SubjectsScreen
import com.msa.studyassistant.ui.theme.MsaTheme

/** مسارات التنقل. */
object Routes {
    const val SETUP = "setup"
    const val SCHEDULE = "schedule?onboarding={onboarding}"
    const val HOME = "home"
    const val SUBJECTS = "subjects"
    const val PROGRESS = "progress"
    const val MISSED = "missed"
    const val SETTINGS = "settings"
    const val SUBJECT_DETAIL = "subject/{subjectId}"
    const val LESSON = "lesson/{subjectId}/{lessonIndex}"

    val topLevel = setOf(HOME, SUBJECTS, PROGRESS, MISSED)

    fun schedule(onboarding: Boolean) = "schedule?onboarding=$onboarding"
    fun subjectDetail(subjectId: String) = "subject/$subjectId"
    fun lesson(subjectId: String, lessonIndex: Int) = "lesson/$subjectId/$lessonIndex"
}

/**
 * جذر التطبيق: الثيم + اتجاه RTL دائم (واجهة عربية) + شريط التنقل السفلي + شاشات التنقل.
 */
@Composable
fun AppRoot(container: AppContainer) {
    val viewModel: MainViewModel = viewModel(factory = container.viewModelFactory)
    val themeMode by container.themeStore.mode.collectAsState()
    val progress by viewModel.progress.collectAsStateWithLifecycle()

    MsaTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route
                val showBottomBar = currentRoute in Routes.topLevel

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            MsaBottomBar(
                                navController = navController,
                                currentRoute = currentRoute,
                                missedCount = progress.missedLessons.size,
                            )
                        }
                    },
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = remember {
                            if (viewModel.state.value.profile == null) Routes.SETUP else Routes.HOME
                        },
                        modifier = Modifier.padding(innerPadding),
                    ) {
                        composable(Routes.SETUP) {
                            SetupScreen(
                                onStart = { profile ->
                                    viewModel.saveProfile(profile)
                                    navController.navigate(Routes.schedule(onboarding = true)) {
                                        popUpTo(Routes.SETUP) { inclusive = true }
                                    }
                                },
                            )
                        }
                        composable(
                            route = Routes.SCHEDULE,
                            arguments = listOf(
                                navArgument("onboarding") { type = NavType.BoolType; defaultValue = true },
                            ),
                        ) { entry ->
                            val onboarding = entry.arguments?.getBoolean("onboarding") ?: true
                            ScheduleScreen(
                                viewModel = viewModel,
                                onboarding = onboarding,
                                onDone = {
                                    if (onboarding) {
                                        navController.navigate(Routes.HOME) { popUpTo(0) { inclusive = true } }
                                    } else {
                                        navController.popBackStack()
                                    }
                                },
                            )
                        }
                        composable(Routes.HOME) {
                            HomeScreen(
                                viewModel = viewModel,
                                themeStore = container.themeStore,
                                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                                onOpenSubject = { navController.navigate(Routes.subjectDetail(it)) },
                            )
                        }
                        composable(Routes.SUBJECTS) {
                            SubjectsScreen(
                                viewModel = viewModel,
                                onOpenSubject = { navController.navigate(Routes.subjectDetail(it)) },
                            )
                        }
                        composable(Routes.PROGRESS) {
                            ProgressScreen(viewModel = viewModel)
                        }
                        composable(Routes.MISSED) {
                            MissedLessonsScreen(
                                viewModel = viewModel,
                                onOpenLesson = { subjectId, lessonIndex ->
                                    navController.navigate(Routes.lesson(subjectId, lessonIndex))
                                },
                            )
                        }
                        composable(Routes.SETTINGS) {
                            SettingsScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() },
                                onEditSchedule = { navController.navigate(Routes.schedule(onboarding = false)) },
                            )
                        }
                        composable(
                            route = Routes.SUBJECT_DETAIL,
                            arguments = listOf(navArgument("subjectId") { type = NavType.StringType }),
                        ) { entry ->
                            val subjectId = entry.arguments?.getString("subjectId").orEmpty()
                            SubjectDetailScreen(
                                viewModel = viewModel,
                                subjectId = subjectId,
                                onBack = { navController.popBackStack() },
                                onOpenLesson = { lessonIndex ->
                                    navController.navigate(Routes.lesson(subjectId, lessonIndex))
                                },
                            )
                        }
                        composable(
                            route = Routes.LESSON,
                            arguments = listOf(
                                navArgument("subjectId") { type = NavType.StringType },
                                navArgument("lessonIndex") { type = NavType.IntType },
                            ),
                        ) { entry ->
                            LessonScreen(
                                viewModel = viewModel,
                                subjectId = entry.arguments?.getString("subjectId").orEmpty(),
                                lessonIndex = entry.arguments?.getInt("lessonIndex") ?: 0,
                                onBack = { navController.popBackStack() },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MsaBottomBar(navController: NavHostController, currentRoute: String?, missedCount: Int) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == Routes.HOME,
            onClick = { navController.navigateTopLevel(Routes.HOME) },
            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
            label = { Text("الرئيسية") },
        )
        NavigationBarItem(
            selected = currentRoute == Routes.SUBJECTS,
            onClick = { navController.navigateTopLevel(Routes.SUBJECTS) },
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
            label = { Text("المواد") },
        )
        NavigationBarItem(
            selected = currentRoute == Routes.PROGRESS,
            onClick = { navController.navigateTopLevel(Routes.PROGRESS) },
            icon = { Icon(painterResource(R.drawable.ic_trending_up), contentDescription = null) },
            label = { Text("التقدم") },
        )
        NavigationBarItem(
            selected = currentRoute == Routes.MISSED,
            onClick = { navController.navigateTopLevel(Routes.MISSED) },
            icon = {
                BadgedBox(
                    badge = {
                        if (missedCount > 0) {
                            Badge { Text(text = missedCount.toString()) }
                        }
                    },
                ) {
                    Icon(Icons.Filled.Warning, contentDescription = null)
                }
            },
            label = { Text("الفائتة") },
        )
    }
}

private fun NavHostController.navigateTopLevel(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
