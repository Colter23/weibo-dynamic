package top.colter.mirai.plugin.weibo.draw

import org.jetbrains.skia.Color
import org.jetbrains.skia.Data
import org.jetbrains.skia.Image
import org.jetbrains.skia.Surface
import org.jetbrains.skia.svg.SVGDOM
import top.colter.mirai.plugin.weibo.tools.loadResourceBytes
import java.util.*


/**
 * 渲染 SVG 图片
 */
fun SVGDOM.makeImage(width: Int, height: Int): Image {
    setContainerSize(width.toFloat(), height.toFloat())
    return Surface.makeRasterN32Premul(width, height).apply { render(canvas) }.makeImageSnapshot()
}

fun loadSVG(bytes: ByteArray): SVGDOM {
    return SVGDOM(Data.makeFromBytes(bytes))
}

/**
 * 从 resources 文件夹中加载 SVG
 */
fun loadResourceSVG(path: String): SVGDOM? {
    return loadResourceBytes(path)?.let { loadSVG(it) }
}

/**
 * 从 resources 文件夹中加载并渲染 SVG
 */
fun loadRenderSVG(path: String, width: Int = 100, height: Int = 100): Image? {
    return loadResourceSVG(path)?.makeImage(width, height)
}

fun ByteArray.makeImage() = Image.makeFromEncoded(this)

fun Color.makeRGB(hex: String): Int {
    require(hex.startsWith("#")) { "Hex format error: $hex" }
    require(hex.length == 7 || hex.length == 9) { "Hex length error: $hex" }
    return when (hex.length) {
        7 -> {
            makeRGB(
                Integer.valueOf(hex.substring(1, 3), 16),
                Integer.valueOf(hex.substring(3, 5), 16),
                Integer.valueOf(hex.substring(5), 16)
            )
        }

        9 -> {
            makeARGB(
                Integer.valueOf(hex.substring(1, 3), 16),
                Integer.valueOf(hex.substring(3, 5), 16),
                Integer.valueOf(hex.substring(5, 7), 16),
                Integer.valueOf(hex.substring(7), 16)
            )
        }

        else -> {
            WHITE
        }
    }
}

fun Color.getRGB(color: Int) = intArrayOf(getR(color), getG(color), getB(color))

fun rgb2hsb(rgbR: Int, rgbG: Int, rgbB: Int): FloatArray {

    val rgb = intArrayOf(rgbR, rgbG, rgbB)
    Arrays.sort(rgb)
    val max = rgb[2]
    val min = rgb[0]
    val hsbB = max / 255.0f
    val hsbS: Float = if (max == 0) 0f else (max - min) / max.toFloat()
    var hsbH = 0f
    if (max == rgbR && rgbG >= rgbB) {
        hsbH = (rgbG - rgbB) * 60f / (max - min) + 0
    } else if (max == rgbR && rgbG < rgbB) {
        hsbH = (rgbG - rgbB) * 60f / (max - min) + 360
    } else if (max == rgbG) {
        hsbH = (rgbB - rgbR) * 60f / (max - min) + 120
    } else if (max == rgbB) {
        hsbH = (rgbR - rgbG) * 60f / (max - min) + 240
    }
    return floatArrayOf(hsbH, hsbS, hsbB)
}

fun hsb2rgb(h: Float, s: Float, v: Float): IntArray {
    var r = 0f
    var g = 0f
    var b = 0f
    val i = (h / 60 % 6).toInt()
    val f = h / 60 - i
    val p = v * (1 - s)
    val q = v * (1 - f * s)
    val t = v * (1 - (1 - f) * s)
    when (i) {
        0 -> {
            r = v
            g = t
            b = p
        }

        1 -> {
            r = q
            g = v
            b = p
        }

        2 -> {
            r = p
            g = v
            b = t
        }

        3 -> {
            r = p
            g = q
            b = v
        }

        4 -> {
            r = t
            g = p
            b = v
        }

        5 -> {
            r = v
            g = p
            b = q
        }

        else -> {}
    }
    return intArrayOf((r * 255.0).toInt(), (g * 255.0).toInt(), (b * 255.0).toInt())
}