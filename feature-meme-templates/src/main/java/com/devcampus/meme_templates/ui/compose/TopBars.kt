package com.devcampus.meme_templates.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devcampus.common_android.ui.theme.TextOutline
import com.devcampus.common_android.ui.theme.colorsScheme
import com.devcampus.meme_templates.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DefaultTopBar(
    onSearchClick: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                text = stringResource(R.string.choose_template_dialog_title),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Icon(
                modifier = Modifier.clickable { onSearchClick() },
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        }

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(R.string.choose_template_dialog_message),
            fontSize = 12.sp,
        )
    }
}

@Composable
internal fun SearchTopBar(
    templatesCount: Int,
    onDismissed: () -> Unit,
    onQueryUpdate: (String) -> Unit,
) {

    var text by remember { mutableStateOf("") }

    val focusRequester = FocusRequester()

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = text,
            singleLine = true,
            onValueChange = {
                text = it
                onQueryUpdate(text)
            },
            label = null,
            leadingIcon = {
                Icon(
                    modifier = Modifier.clickable { onDismissed() },
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                )
            },
            colors = TextFieldDefaults.colors().copy(
                focusedContainerColor = colorsScheme().surfaceContainerLow,
                unfocusedContainerColor = colorsScheme().surfaceContainerLow,
                focusedIndicatorColor = TextOutline,
                unfocusedIndicatorColor = TextOutline,
            )
        )

        val message = if (templatesCount == 0) {
            stringResource(R.string.no_templates_found)
        } else {
            pluralStringResource(R.plurals.templates_count, templatesCount, templatesCount)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.labelSmall,
            color = colorsScheme().textOutline,
        )

        LaunchedEffect(Unit) { focusRequester.requestFocus() }
    }
}
