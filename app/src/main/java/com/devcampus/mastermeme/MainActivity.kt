package com.devcampus.mastermeme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.devcampus.common_android.ui.theme.MasterMemeTheme
import com.devcampus.create_meme.ui.CreateMemeDestination
import com.devcampus.create_meme.ui.compose.CreateMemeScreen
import com.devcampus.meme_templates.ui.MemeTemplatesDestination
import com.devcampus.meme_templates.ui.compose.MemeTemplatesBottomSheet
import com.devcampus.memes_list.ui.MemeScreenDestination
import com.devcampus.memes_list.ui.compose.MemesScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()

            MasterMemeTheme {
                NavHost(
                    navController = navController,
                    startDestination = MemeScreenDestination
                ) {

                    composable<MemeScreenDestination> {
                        MemesScreen(
                            onAddClick = {
                                navController.navigate(MemeTemplatesDestination)
                            }
                        )
                    }

                    dialog<MemeTemplatesDestination> {
                        MemeTemplatesBottomSheet(
                            onSelected = { template ->
                                navController.navigate(CreateMemeDestination(template))
                            },
                            onDismissed = { navController.navigateUp() },
                        )
                    }

                    composable<CreateMemeDestination> { backStackEntry ->

                        val params: CreateMemeDestination = backStackEntry.toRoute()

                        CreateMemeScreen(
                            templateAsset = params.templateAssetPath,
                            onClickUp = { navController.navigateUp() }
                        )

                    }
                }
            }
        }
    }
}
