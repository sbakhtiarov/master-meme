package com.devcampus.create_meme.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.util.SizeF
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.applyCanvas
import com.devcampus.create_meme.domain.Decor
import com.devcampus.create_meme.domain.MemeFileSaver
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

class MemeFileSaverImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : MemeFileSaver {

    private companion object {
        private const val MEMES_FOLDER = "memes"
    }

    override suspend fun copyMemeAsset(
        assetPath: String,
        editorCanvasSize: SizeF,
        decorList: List<Decor>,
    ) {
        withContext(Dispatchers.IO) {

            val assetName = assetPath.split("/").last()

            val memesDirectory = File(context.getExternalFilesDir(null), MEMES_FOLDER)
            val destFile = File(memesDirectory, "meme_${UUID.randomUUID()}_$assetName")

            memesDirectory.mkdirs()

            val options = BitmapFactory.Options()
            options.inMutable = true

            val bitmap = context.assets.open("templates/$assetName").use { input ->
                BitmapFactory.decodeStream(input, null, options)
            } ?: error("Failed to load bitmap")

            val sx = bitmap.width / editorCanvasSize.width
            val sy = bitmap.height / editorCanvasSize.height

            bitmap.applyCanvas {
                decorList.forEach { decor ->
                    when (decor) {
                        is Decor.TextDecor -> {

                            val decorTypeface = ResourcesCompat.getFont(context, decor.fontResId) ?: error("Failed to load typeface")
                            val fontSize = decor.fontSize * sx
                            val textPaint = Paint().apply {
                                typeface = decorTypeface
                                textSize = fontSize
                                color = decor.color
                                isAntiAlias = true
                            }

                            drawText(decor.text, decor.topLeft.x * sx, decor.topLeft.y * sy + fontSize, textPaint)

                            if (decor.isStroke) {

                                textPaint.strokeWidth = 1.5f
                                textPaint.color = Color.BLACK
                                textPaint.style = Paint.Style.STROKE

                                drawText(decor.text, decor.topLeft.x * sx, decor.topLeft.y * sy + fontSize, textPaint)
                            }
                        }
                    }
                }
            }

            destFile.outputStream().use { output ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
            }
        }
    }
}
