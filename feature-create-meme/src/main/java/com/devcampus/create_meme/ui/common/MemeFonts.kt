package com.devcampus.create_meme.ui.common

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.devcampus.create_meme.R

object MemeFonts {

    val fonts = listOf(
        MemeFontFamily("Impact", FontFamily(Font(R.font.impact_regular, FontWeight.Normal)), isStroke = true),
        MemeFontFamily("Bahiana", FontFamily(Font(R.font.bahiana_regular, FontWeight.Normal))),
        MemeFontFamily("Barriecito", FontFamily(Font(R.font.barriecito_regular, FontWeight.Normal))),
        MemeFontFamily("Bungee Shade", FontFamily(Font(R.font.bungee_shade_regular, FontWeight.Normal)), baseFontSize = 26.sp),
        MemeFontFamily("Henny Penny", FontFamily(Font(R.font.henny_penny_regular, FontWeight.Normal)), baseFontSize = 30.sp),
        MemeFontFamily("Knewave", FontFamily(Font(R.font.knewave_regular, FontWeight.Normal))),
        MemeFontFamily("Londrina", FontFamily(Font(R.font.londrina_outline_regular, FontWeight.Normal))),
        MemeFontFamily("Londrina Shadow", FontFamily(Font(R.font.londrina_shadow_regular, FontWeight.Normal))),
        MemeFontFamily("Modak", FontFamily(Font(R.font.modak_regular, FontWeight.Normal))),
        MemeFontFamily("Monoton", FontFamily(Font(R.font.monoton_regular, FontWeight.Normal)), baseFontSize = 30.sp),
        MemeFontFamily("Nabla", FontFamily(Font(R.font.nabla_regular, FontWeight.Normal)), colored = true),
        MemeFontFamily("Rammetto", FontFamily(Font(R.font.rammetto_one_regular, FontWeight.Normal)), baseFontSize = 26.sp),
        MemeFontFamily(
            "Rubik Doodle Shadow",
            FontFamily(Font(R.font.rubik_doodle_shadow_regular, FontWeight.Normal)),
            baseFontSize = 30.sp
        ),
        MemeFontFamily("Silkscreen", FontFamily(Font(R.font.silkscreen_regular, FontWeight.Normal)), baseFontSize = 30.sp),
        MemeFontFamily("Vast Shadow", FontFamily(Font(R.font.vast_shadow_regular, FontWeight.Normal)), baseFontSize = 26.sp),
    )
}

data class MemeFontFamily(
    val name: String,
    val fontFamily: FontFamily,
    val isStroke: Boolean = false,
    val baseFontSize: TextUnit = 38.sp,
    val colored: Boolean = false,
)