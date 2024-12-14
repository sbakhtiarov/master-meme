import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object MemeIcons {
    val DecorDelete: ImageVector
        get() {
            if (_IconDelete != null) {
                return _IconDelete!!
            }
            _IconDelete = ImageVector.Builder(
                name = "IconDelete",
                defaultWidth = 20.dp,
                defaultHeight = 20.dp,
                viewportWidth = 20f,
                viewportHeight = 20f
            ).apply {
                path(
                    fill = SolidColor(Color(0xFFB3261E)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(20f, 10f)
                    arcTo(10f, 10f, 0f, isMoreThanHalf = false, isPositiveArc = true, 10f, 20f)
                    arcTo(10f, 10f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, 10f)
                    arcTo(10f, 10f, 0f, isMoreThanHalf = false, isPositiveArc = true, 20f, 10f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFFFFFF)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(14.1969f, 6.74332f)
                    curveTo(14.45650f, 6.48370f, 14.45650f, 6.06290f, 14.19690f, 5.80330f)
                    curveTo(13.93730f, 5.54380f, 13.51650f, 5.54380f, 13.25690f, 5.80330f)
                    lineTo(10.0002f, 9.05999f)
                    lineTo(6.74357f, 5.80332f)
                    curveTo(6.4840f, 5.54380f, 6.06310f, 5.54380f, 5.80360f, 5.80330f)
                    curveTo(5.5440f, 6.06290f, 5.5440f, 6.48370f, 5.80360f, 6.74330f)
                    lineTo(9.06023f, 9.99999f)
                    lineTo(5.80357f, 13.2567f)
                    curveTo(5.5440f, 13.51620f, 5.5440f, 13.93710f, 5.80360f, 14.19670f)
                    curveTo(6.06310f, 14.45620f, 6.4840f, 14.45620f, 6.74360f, 14.19670f)
                    lineTo(10.0002f, 10.94f)
                    lineTo(13.2569f, 14.1967f)
                    curveTo(13.51650f, 14.45620f, 13.93730f, 14.45620f, 14.19690f, 14.19670f)
                    curveTo(14.45650f, 13.93710f, 14.45650f, 13.51620f, 14.19690f, 13.25670f)
                    lineTo(10.9402f, 9.99999f)
                    lineTo(14.1969f, 6.74332f)
                    close()
                }
            }.build()
            return _IconDelete!!
        }

    private var _IconDelete: ImageVector? = null

    val SimCardDownload: ImageVector
        get() {
            if (_Sim_card_download != null) {
                return _Sim_card_download!!
            }
            _Sim_card_download = ImageVector.Builder(
                name = "Sim_card_download",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 960f,
                viewportHeight = 960f
            ).apply {
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(480f, 680f)
                    lineToRelative(160f, -160f)
                    lineToRelative(-56f, -56f)
                    lineToRelative(-64f, 62f)
                    verticalLineToRelative(-166f)
                    horizontalLineToRelative(-80f)
                    verticalLineToRelative(166f)
                    lineToRelative(-64f, -62f)
                    lineToRelative(-56f, 56f)
                    close()
                    moveTo(240f, 880f)
                    quadToRelative(-33f, 0f, -56.5f, -23.5f)
                    reflectiveQuadTo(160f, 800f)
                    verticalLineToRelative(-480f)
                    lineToRelative(240f, -240f)
                    horizontalLineToRelative(320f)
                    quadToRelative(33f, 0f, 56.5f, 23.5f)
                    reflectiveQuadTo(800f, 160f)
                    verticalLineToRelative(640f)
                    quadToRelative(0f, 33f, -23.5f, 56.5f)
                    reflectiveQuadTo(720f, 880f)
                    close()
                    moveToRelative(0f, -80f)
                    horizontalLineToRelative(480f)
                    verticalLineToRelative(-640f)
                    horizontalLineTo(434f)
                    lineTo(240f, 354f)
                    close()
                    moveToRelative(0f, 0f)
                    horizontalLineToRelative(480f)
                    close()
                }
            }.build()
            return _Sim_card_download!!
        }

    private var _Sim_card_download: ImageVector? = null
}
