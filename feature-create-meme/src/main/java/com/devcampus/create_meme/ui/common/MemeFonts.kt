package com.devcampus.create_meme.ui.common

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.devcampus.create_meme.R

object MemeFonts {

    val fonts = listOf(
        memeFont("Impact", R.font.impact_regular, isStroke = true),
        memeFont("Bahiana", R.font.bahiana_regular),
        memeFont("Barriecito", R.font.barriecito_regular),
        memeFont("Bungee Shade", R.font.bungee_shade_regular, baseFontSize = 26.sp),
        memeFont("Henny Penny", R.font.henny_penny_regular, baseFontSize = 30.sp),
        memeFont("Knewave", R.font.knewave_regular),
        memeFont("Londrina", R.font.londrina_outline_regular),
        memeFont("Londrina Shadow", R.font.londrina_shadow_regular),
        memeFont("Modak", R.font.modak_regular),
        memeFont("Monoton", R.font.monoton_regular, baseFontSize = 30.sp),
        memeFont("Nabla", R.font.nabla_regular, colored = true),
        memeFont("Rammetto", R.font.rammetto_one_regular, baseFontSize = 26.sp),
        memeFont("Rubik Doodle Shadow", R.font.rubik_doodle_shadow_regular, baseFontSize = 30.sp),
        memeFont("Silkscreen", R.font.silkscreen_regular, baseFontSize = 30.sp),
        memeFont("Vast Shadow", R.font.vast_shadow_regular, baseFontSize = 26.sp),
    )

    private fun memeFont(name: String, resId: Int, baseFontSize: TextUnit = 38.sp, isStroke: Boolean = false, colored: Boolean = false) =
        MemeFontFamily(name, resId, FontFamily(Font(resId, FontWeight.Normal)), isStroke, baseFontSize, colored)

}

data class MemeFontFamily(
    val name: String,
    val fontResId: Int,
    val fontFamily: FontFamily,
    val isStroke: Boolean = false,
    val baseFontSize: TextUnit = 38.sp,
    val colored: Boolean = false,
)