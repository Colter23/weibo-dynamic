package top.colter.mirai.plugin.weibo.draw

import org.jetbrains.skia.Color
import org.jetbrains.skia.Image
import top.colter.mirai.plugin.weibo.WeiboConfig
import top.colter.skiko.*
import top.colter.skiko.data.Gradient
import top.colter.skiko.layout.Layout
import top.colter.skiko.layout.View


suspend fun WeiboDraw(draw: suspend Layout.() -> Unit): Image? {

    val color = WeiboConfig.imageConfig.defaultColor
    val colors = color.split(";").filter { it != "" }
    Dp.factor = WeiboConfig.imageConfig.factor

    val image = View(
        modifier = Modifier()
            .width(1000.dp)
            .padding(30.dp)
            .background(
                color = Color.makeRGB(colors.first()),
                gradient = if (colors.size == 1) null
                else Gradient(10, listOf(Color.makeRGB(colors.first()), Color.makeRGB(colors[1])))
            )
    ) {
        draw()
    }

    return image
}