package com.devcampus.memes_list.data

import android.os.Build
import android.os.FileObserver
import java.io.File

/**
 * Observe application memes folder
 */
@Suppress("DEPRECATION")
internal fun File.startWatching(onUpdate: () -> Unit) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        object : FileObserver(this, CREATE or DELETE) {
            override fun onEvent(event: Int, file: String?) {
                onUpdate()
            }
        }
    } else {
        object : FileObserver(this.absolutePath, CREATE or DELETE) {
            override fun onEvent(event: Int, file: String?) {
                onUpdate()
            }
        }
    }.apply {
        startWatching()
    }
