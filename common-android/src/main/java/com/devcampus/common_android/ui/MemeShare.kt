package com.devcampus.common_android.ui

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

object MemeShare {
    fun showShareDialog(context: Context, paths: List<String>) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            putParcelableArrayListExtra(
                Intent.EXTRA_STREAM, ArrayList(
                paths.mapNotNull {
                    try {
                        FileProvider.getUriForFile(
                            context,
                            "com.devcampus.mastermeme.fileprovider",
                            File(it)
                        )
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                }
            ))
            type = "image/*"
        }
        context.startActivity(Intent.createChooser(shareIntent, null))
    }
}