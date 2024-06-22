package top.colter.mirai.plugin.weibo.draw.component

import org.jetbrains.skia.*
import top.colter.mirai.plugin.weibo.draw.makeImage
import top.colter.mirai.plugin.weibo.tools.loadResourceBytes
import top.colter.skiko.*
import top.colter.skiko.data.LayoutAlignment
import top.colter.skiko.layout.*
import java.io.File

/**
 * 作者组件
 */
fun Layout.Author(
    face: Image,
    pendant: Image? = null,
    verify: Int = -1,
    name: String,
    time: String,
    ornament: Image,
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

    val ratio = 0.56f

    Avatar(
        face = face,
        pendant = pendant,
        badge = badgeImage,
        modifier = Modifier().height(modifier.height).margin(15.dp)
    )
    Column(
        modifier = Modifier().fillWidth().fillMaxHeight() // .background(Color.GREEN)
    ) {
        Box(
            modifier = Modifier().fillMaxWidth().fillRatioHeight(ratio) // .background(Color.RED)
        ) {
            Text(
                text = name,
                color = Color.makeRGB(251, 114, 153),
                fontSize = 36.dp,
                fontStyle = MEDIUM,
                fontFamily = FontUtils.defaultFont?.familyName ?: "",
//                    fontFamily = "HarmonyOS Sans",
                alignment = LayoutAlignment.CENTER_LEFT,
            )
        }
        Box(
            modifier = Modifier().fillMaxWidth().fillRatioHeight(1f - ratio) // .background(Color.YELLOW)
        ) {
            Text(
                text = time,
                color = Color.makeRGB(156, 156, 156),
                fontSize = 28.dp,
                fontStyle = MEDIUM,
                fontFamily = FontUtils.defaultFont?.familyName ?: "",
//                    fontFamily = "HarmonyOS Sans",
                alignment = LayoutAlignment.CENTER_LEFT,
            )
        }

    }

    Decorate(
        image = ornament,
        modifier = Modifier().height(modifier.height).margin(15.dp)
    )

}

val MEDIUM: FontStyle
    get() = FontStyle(FontWeight.MEDIUM, FontWidth.NORMAL, FontSlant.UPRIGHT)