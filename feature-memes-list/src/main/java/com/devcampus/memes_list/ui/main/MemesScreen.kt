package com.devcampus.memes_list.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.devcampus.common_android.ui.theme.SurfaceContainer
import com.devcampus.memes_list.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemesScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.your_memes),
                        fontSize = 24.sp,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = SurfaceContainer
                ),
                actions = {
                    SortOptionsDropdown {
                        // TODO: handle sort option selection
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            EmptyScreen()
        }
    }
}
