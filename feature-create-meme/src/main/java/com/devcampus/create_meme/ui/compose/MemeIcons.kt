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
}
