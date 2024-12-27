package com.devcampus.mastermeme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.devcampus.common_android.ui.theme.MasterMemeTheme
import com.devcampus.create_meme.ui.CreateMemeDestination
import com.devcampus.create_meme.ui.compose.CreateMemeScreen
import com.devcampus.memes_list.ui.MemePreviewScreenDestination
import com.devcampus.memes_list.ui.MemeScreenDestination
import com.devcampus.memes_list.ui.compose.MemePreviewScreen
import com.devcampus.memes_list.ui.compose.MemesScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            val navController = rememberNavController()

            MasterMemeTheme {
                SharedTransitionLayout {
                    NavHost(
                        navController = navController,
                        startDestination = MemeScreenDestination
                    ) {

                        composable<MemeScreenDestination> {
                            MemesScreen(
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedContentScope = this,
                                showMemePreview = { path ->
                                    navController.navigate(MemePreviewScreenDestination(path))
                                },
                                showMemeEditor = { template ->
                                    navController.navigate(CreateMemeDestination(template))
                                },
                            )
                        }

                        composable<CreateMemeDestination> { backStackEntry ->

                            val params: CreateMemeDestination = backStackEntry.toRoute()

                            CreateMemeScreen(
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedContentScope = this,
                                templateAsset = params.templateAssetPath,
                                onClickUp = { navController.navigateUp() }
                            )

                        }

                        composable<MemePreviewScreenDestination> { backStackEntry ->

                            val params: MemePreviewScreenDestination = backStackEntry.toRoute()

                            MemePreviewScreen(
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedContentScope = this,
                                path = params.path,
                                onClickUp = { navController.navigateUp() }
                            )
                        }
                    }
                }
            }
        }
    }
}
