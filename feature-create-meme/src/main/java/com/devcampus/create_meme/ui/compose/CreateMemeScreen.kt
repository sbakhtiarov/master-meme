package com.devcampus.create_meme.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.devcampus.common_android.ui.theme.Primary
import com.devcampus.common_android.ui.theme.SurfaceContainer
import com.devcampus.create_meme.R
import com.devcampus.create_meme.ui.CloseScreen
import com.devcampus.create_meme.ui.CreateMemeViewModel
import com.devcampus.create_meme.ui.Intent
import com.devcampus.create_meme.ui.Intent.OnBackPress
import com.devcampus.create_meme.ui.ShowLeaveConfirmation
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMemeScreen(
    templateAsset: String,
    onClickUp: () -> Unit,
) {

    val viewModel : CreateMemeViewModel = hiltViewModel()
    val sendIntent: (Intent) -> Unit = remember { { viewModel.onIntent(it) } }

    var showLeaveConfirmation by remember { mutableStateOf(false) }

    BackHandler { sendIntent(OnBackPress) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(R.string.new_meme))
                },
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .size(44.dp)
                            .padding(8.dp)
                            .clickable { sendIntent(OnBackPress) },
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = SurfaceContainer
                ),
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Row {
                        Icon(
                            modifier = Modifier.alpha(0.3f),
                            painter = painterResource(R.drawable.ic_undo),
                            tint = Primary,
                            contentDescription = null,
                        )
                        Icon(
                            modifier = Modifier.alpha(0.3f),
                            painter = painterResource(R.drawable.ic_redo),
                            tint = Primary,
                            contentDescription = null
                        )
                    }

                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f))

                    OutlinedButton(
                        onClick = {}
                    ) {
                        Text(text = "Add text")
                    }

                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f))

                    Button(
                        onClick = {
                            sendIntent(Intent.OnSaveMeme(templateAsset))
                        }
                    ) {
                        Text(text = "Save meme")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxWidth(),
                model = templateAsset,
                contentDescription = null
            )
        }
    }

    if (showLeaveConfirmation) {
        ConfirmationDialog(
            onConfirm = {
                showLeaveConfirmation = false
                onClickUp()
            },
            onCancel = {
                showLeaveConfirmation = false
            }
        )
    }

    LaunchedEffect(Unit) {
        viewModel.actions.collectLatest { action ->
            when (action) {
                ShowLeaveConfirmation -> showLeaveConfirmation = true
                CloseScreen -> onClickUp()
            }
        }
    }
}
