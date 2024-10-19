package top.colter.mirai.plugin.weibo.draw

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.client.j2se.MatrixToImageConfig
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.*
import org.jetbrains.skia.Color
import org.jetbrains.skia.Image
import org.jetbrains.skia.paragraph.TextStyle
import org.jetbrains.skiko.toBitmap
import top.colter.mirai.plugin.weibo.WeiboConfig
import top.colter.mirai.plugin.weibo.data.WeiboDynamic
import top.colter.mirai.plugin.weibo.data.WeiboEmoji
import top.colter.mirai.plugin.weibo.data.WeiboFullContent
import top.colter.mirai.plugin.weibo.draw.component.Author
import top.colter.mirai.plugin.weibo.draw.component.SmallAuthor
import top.colter.mirai.plugin.weibo.tools.*
import top.colter.skiko.*
import top.colter.skiko.data.RichParagraphBuilder
import top.colter.skiko.data.Shadow
import top.colter.skiko.layout.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.coroutines.coroutineContext


val formatterOr = DateTimeFormatter.ofPattern("EEE LLL dd HH:mm:ss Z yyyy", Locale.US)
val formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss")

val emoji by lazy { String(loadResourceBytes("emoji.json")!!).run {
    this.decode<List<WeiboEmoji>>()
}}

val color by lazy { WeiboConfig.imageConfig.defaultColor }
val colors by lazy { color.split(";", "；").filter { it != "" }.map { Color.makeRGB(it.trim()) } }

suspend fun DynamicDraw(dynamic: WeiboDynamic): Image? {
    val draw = WeiboDraw {
        DynamicView(dynamic)
    }
    cacheImage(draw, "${dynamic.user?.id}/${dynamic.id}.png", CacheType.DRAW_DYNAMIC)
    return draw
}

suspend fun Layout.DynamicView(dynamic: WeiboDynamic) {

    val imgMap = mutableMapOf<String, ByteArray?>()
    val imgList = mutableListOf<ByteArray?>()
    CoroutineScope(coroutineContext).launch {
        val list = mutableListOf<Deferred<Pair<String, ByteArray?>>>()
        val img = mutableListOf<Deferred<ByteArray?>>()

        if (dynamic.user?.avatar != null) {
            list.add(async { Pair("face", getOrDownload(dynamic.user?.avatar!!, CacheType.USER) ) })
        }
        if (dynamic.pageInfo?.pagePic != null) {
            list.add(async { Pair("pagePic", getOrDownload(dynamic.pageInfo?.pagePic!!, CacheType.IMAGES) ) })
        }
        if (!dynamic.pics.isNullOrEmpty()) {
            dynamic.pics!!.forEach {
                img.add(async { getOrDownload("https://wx4.sinaimg.cn/orj1080/${it}.jpg", CacheType.IMAGES) })
            }
        }

        list.awaitAll().forEach { imgMap[it.first] = it.second }
        img.awaitAll().forEach { imgList.add(it) }
    }.join()

    val face = imgMap["face"]?.makeImage()!!
    val verify = dynamic.user?.verifiedType!!

    val name = dynamic.user?.name!!

    val parsedDate = LocalDateTime.parse(dynamic.createdTime, formatterOr)
    val time = formatter.format(parsedDate)

    var content = dynamic.content
    val dynamicContent = weiboClient.get<WeiboFullContent>("https://weibo.com/ajax/statuses/longtext?id=${dynamic.id}")
    if (dynamicContent.data?.longTextContent?.isNotBlank() == true) {
        content = dynamicContent.data?.longTextContent
    }

    if (content != null) {
        content = content.replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&nbsp;", " ")
    }

    val style = TextStyle().setColor(Color.BLACK).setFontSize(30.px).setFontFamily(FontUtils.defaultFont!!.familyName)
    val linkStyle = TextStyle().setColor(Color.makeRGB(235,115, 64)).setFontSize(30.px).setFontFamily(FontUtils.defaultFont!!.familyName)
    val paragraph = RichParagraphBuilder(style)

    var urlLink = ""

    var i = 0
    var j = 0
    var topicFlag = false
//    var emojiFlag = false
    var atFlag = false
    if (content != null) {
        content = content.removeSuffix(" \u200B\u200B\u200B")
        if (!dynamic.urlStruct.isNullOrEmpty() && content.endsWith(dynamic.urlStruct!![0].shortUrl)) {
            content = content.removeSuffix(dynamic.urlStruct!![0].shortUrl)
            urlLink = dynamic.urlStruct!![0].urlTitle
        }

        for (c in content) {
            when (c) {
                '#' -> {
                    if (topicFlag) {
                        paragraph.addText(content.substring(j, i + 1), linkStyle)
                        j = i + 1
                    }else {
                        if (i != j) {
                            paragraph.addText(content.substring(j, i))
                        }
                        j = i
                    }
                    topicFlag = !topicFlag
                }
                '[' -> {
                    if (!topicFlag) {
                        if (i != j) {
                            paragraph.addText(content.substring(j, i))
                        }
                        j = i
                    }
                }
                ']' -> {
                    if (!topicFlag) {
                        val em = content.substring(j, i + 1)
                        try {
                            val eu = emoji.find { it.phrase == em }!!.url
                            paragraph.addEmoji(em, getOrDownload(eu, CacheType.EMOJI)!!.makeImage())
                            j = i + 1
                        }catch (e: Exception) {
                            logger.warning("emoji加载失败: $em", e)
                        }
                    }
                }

                '@' -> {
                    if (!topicFlag) {
                        atFlag = true
                        if (i != j) {
                            paragraph.addText(content.substring(j, i))
                        }
                        j = i
                    }
                }
                ':', ' ' -> {
                    if (!topicFlag && atFlag) {
                        atFlag = false
                        paragraph.addText(content.substring(j, i + 1), linkStyle)
                        j = i + 1
                    }
                }
            }
            i++
        }
        if (content.length > j) {
            paragraph.addText(content.substring(j, content.length))
        }
    }

    if (urlLink != "") {
        paragraph.addText("🔗$urlLink", linkStyle)
    }


    val shadow = if (containsEnv("forward")) Shadow.ELEVATION_5 else Shadow.ELEVATION_7
    val margin = if (containsEnv("forward")) 20 else 0

    Column(modifier = Modifier()
        .fillMaxWidth()
        .margin(top = margin.dp)
        .padding(20.dp)
        .background(Color.WHITE.withAlpha(0.6f))
        .border(3.dp, 15.dp)
        .shadows(shadow)
    ) {

        if (containsEnv("forward")) {
            SmallAuthor(
                face = face,
                verify = verify,
                name = name,
                time = time,
                modifier = Modifier().fillMaxWidth().height(50.dp).margin(horizontal = 5.dp, vertical = 10.dp) // .background(Color.RED)
            )
        } else {
            Author(
                face = face,
                pendant = null,
                verify = verify,
                name = name,
                time = time,
                ornament = qrCode("https://weibo.com/${dynamic.user?.id!!}/${dynamic.id}", 120, colors.first()),
                modifier = Modifier().fillMaxWidth().height(100.dp).margin(horizontal = (-15).dp, vertical = 10.dp) // .background(Color.RED)
//                modifier = Modifier().fillMaxWidth().height(100.dp).margin(top = 10.dp, right = (-15).dp, bottom = 30.dp, left = (-15).dp) // .background(Color.RED)
            )
        }

        if (dynamic.content != null) {
//            val style = TextStyle().setColor(Color.BLACK).setFontSize(30.px).setFontFamily(FontUtils.defaultFont!!.familyName)
//            val linkStyle = TextStyle().setColor(Color.makeRGB(23, 139, 207)).setFontSize(30.px).setFontFamily(FontUtils.defaultFont!!.familyName)
//            val paragraph = RichParagraphBuilder(style)
//            paragraph.addText(content!!)

            RichText(
                paragraph = paragraph.build(),
                modifier = Modifier().margin(vertical = 20.dp)
            )
        }

        val imgModifier = Modifier().background(Color.WHITE.withAlpha(0.6f)).border(2.dp, 10.dp).shadows(Shadow.ELEVATION_1)
        if (imgList.isNotEmpty()) {
            val imgs = imgList.map { it?.makeImage()!! }
            if (imgs.size == 1) {
                Image(image = imgs.first(), modifier = imgModifier)
            } else {
                val lineCount = if (imgs.size == 2 || imgs.size == 4) 2 else 3
                Grid(maxLineCount = lineCount, space = 15.dp, modifier = Modifier().fillMaxWidth()) {
                    for (element in imgs) Image(element, modifier = imgModifier)
                }
            }
        }

        if (imgMap.containsKey("pagePic")) {
            Image(image = imgMap["pagePic"]!!.makeImage(), modifier = imgModifier)
        }

        dynamic.origin?.let {
            putEnv("forward", true)
            DynamicView(it)
            removeEnv("forward")
        }

    }

}

fun qrCode(url: String, width: Int, color: Int): Image {
    val qrCodeWriter = QRCodeWriter()

    val bitMatrix = qrCodeWriter.encode(
        url, BarcodeFormat.QR_CODE, width, width,
        mapOf(
            EncodeHintType.MARGIN to 0
        )
    )

    val c = Color.getRGB(color)
    val cc = c[0] + c[1] + c[2]
    val ccc = if (cc > 382) {
        val hsb = rgb2hsb(c[0], c[1], c[2])
        hsb[1] = if (hsb[1] + 0.25f > 1f) 1f else hsb[1] + 0.25f
        val rgb = hsb2rgb(hsb[0], hsb[1], hsb[2])
        Color.makeRGB(rgb[0], rgb[1], rgb[2])
    } else {
        color
    }

    val config = MatrixToImageConfig(ccc, Color.makeARGB(0, 255, 255, 255))

    return Image.makeFromBitmap(MatrixToImageWriter.toBufferedImage(bitMatrix, config).toBitmap())
}