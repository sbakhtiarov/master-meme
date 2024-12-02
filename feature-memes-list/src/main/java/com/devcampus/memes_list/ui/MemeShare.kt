package com.devcampus.memes_list.ui

import android.content.Context
import androidx.core.content.FileProvider
import java.io.File

object MemeShare {

    fun showShareDialog(context: Context, paths: List<String>) {
        val shareIntent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND_MULTIPLE
            putParcelableArrayListExtra(android.content.Intent.EXTRA_STREAM, ArrayList(
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
        context.startActivity(android.content.Intent.createChooser(shareIntent, null))
    }
}