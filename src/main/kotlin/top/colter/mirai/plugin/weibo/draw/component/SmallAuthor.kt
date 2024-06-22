package top.colter.mirai.plugin.weibo.draw.component

import org.jetbrains.skia.Color
import org.jetbrains.skia.Image
import top.colter.mirai.plugin.weibo.draw.makeImage
import top.colter.mirai.plugin.weibo.tools.loadResourceBytes
import top.colter.skiko.*
import top.colter.skiko.data.LayoutAlignment
import top.colter.skiko.layout.Layout
import top.colter.skiko.layout.Row
import top.colter.skiko.layout.Text
import java.io.File

/**
 * 作者小组件
 */
fun Layout.SmallAuthor(
    face: Image,
    verify: Int = -1,
    name: String,
    time: String,
    alignment: LayoutAlignment = LayoutAlignment.CENTER,
    modifier: Modifier
) = Row (
    alignment = alignment,
    modifier = modifier
) {
    require(modifier.height.isNotNull()) { "必须指定高度" }

    val badgeImage = when (verify) {
        0 -> loadResourceBytes("icon/PERSONAL_OFFICIAL_VERIFY.png")?.makeImage()
        7 -> loadResourceBytes("icon/ORGANIZATION_OFFICIAL_VERIFY.png")?.makeImage()
        else -> null
    }

    Avatar(
        face = face,
        badge = badgeImage,
        modifier = Modifier().height(modifier.height).margin(15.dp)
    )
    Row(
        modifier = Modifier().fillMaxWidth().fillHeight(), // .background(Color.GREEN),
        alignment = LayoutAlignment.CENTER_LEFT
    ) {
        Text(
            text = name,
            color = Color.makeRGB(251, 114, 153),
            fontSize = 30.dp,
            fontStyle = MEDIUM,
            fontFamily = FontUtils.defaultFont?.familyName ?: "",
            alignment = LayoutAlignment.CENTER_LEFT,
            modifier = Modifier().margin(right = 15.dp)
        )
        Text(
            text = time,
            color = Color.makeRGB(156, 156, 156),
            fontSize = 22.dp,
            fontStyle = MEDIUM,
            fontFamily = FontUtils.defaultFont?.familyName ?: "",
            alignment = LayoutAlignment.CENTER_LEFT,
        )
    }
}
