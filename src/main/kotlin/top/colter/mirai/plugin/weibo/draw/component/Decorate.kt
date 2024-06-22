package top.colter.mirai.plugin.weibo.draw.component

import org.jetbrains.skia.Color
import org.jetbrains.skia.Image
import top.colter.skiko.FontUtils
import top.colter.skiko.Modifier
import top.colter.skiko.data.LayoutAlignment
import top.colter.skiko.dp
import top.colter.skiko.layout.Box
import top.colter.skiko.layout.Image
import top.colter.skiko.layout.Layout
import top.colter.skiko.layout.Text
import top.colter.skiko.width

/**
 * 装饰组件
 */
fun Layout.Decorate(
    image: Image,
    numStr: String? = null,
    color: Int? = null,
    alignment: LayoutAlignment = LayoutAlignment.CENTER,
    modifier: Modifier
) = Box (
    alignment = alignment,
    modifier = modifier
) {
    require(modifier.height.isNotNull()) { "必须指定高度" }

//    val cardHeight = if (numStr == null) modifier.height * 0.6f else modifier.height
//    val cardWidth = image.width.dp * cardHeight.px / image.height.dp
//
//    modifier.width(if (numStr != null) cardWidth * 0.66f else cardWidth)

    Image(
        image = image,
        alignment = LayoutAlignment.CENTER_RIGHT,
        modifier = Modifier().width(image.width.dp)
    )
}
