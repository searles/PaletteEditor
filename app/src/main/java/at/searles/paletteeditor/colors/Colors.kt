package at.searles.paletteeditor.colors

import android.graphics.Color
import kotlin.math.min

/**
 * Colors can come in two flavors: as integers in AARRGGBB-Format, or
 * as float[4]-arrays in rgba-format or laba-format.
 */
object Colors {
    fun brightness(rgb: Int): Float {
        val r = rgb and 0x00ff0000 shr 16
        val g = rgb and 0x0000ff00 shr 8
        val b = rgb and 0x000000ff
        return (0.2126f * r + 0.7152f * g + 0.0722f * b) / 255f
    }

    fun toColorString(color: Int): String {
        return if (color and -0x1000000 == -0x1000000) { // alpha is 100%
            String.format("#%06x", color and 0xffffff)
        } else {
            String.format("#%08x", color)
        }
    }

    private fun toHexDigit(digit: Char): Int {
        if (digit in '0'..'9') return digit - '0' else if (digit in 'A'..'F') return digit - 'A' + 10 else if (digit in 'a'..'f') return digit - 'a' + 10
        return -1
    }

    fun fromColorString(colorString: String): Int {
        var color = colorString
        if (color.startsWith("#")) {
            color = color.substring(1)
            var c = -0x1
            if (color.length == 3 || color.length == 4) {
                for (element in color) {
                    val d =
                        toHexDigit(element)
                    if (d == -1) throw ColorFormatException(
                        "bad format: $color"
                    )
                    c = c shl 4
                    c = c or d
                    c = c shl 4
                    c = c or d
                }
                return c
            } else if (color.length == 6 || color.length == 8) {
                for (element in color) {
                    val d =
                        toHexDigit(element)
                    if (d == -1) throw ColorFormatException(
                        "bad format: $color"
                    )
                    c = c shl 4
                    c = c or d
                }
                return c
            }
        }
        throw ColorFormatException("bad format: $color")
    }

    fun transparent(alpha: Float, color: Int): Int {
        return Color.argb((Color.alpha(color) * alpha).toInt(), Color.red(color), Color.green(color), Color.blue(color))
    }

    fun toGray(color: Int): Int {
        val brightness = min(255, (brightness(color) * 256f).toInt())
        return Color.argb(Color.alpha(color), brightness, brightness, brightness)
    }
}