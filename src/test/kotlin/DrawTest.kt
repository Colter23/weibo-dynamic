import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.enable
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.load
import net.mamoe.mirai.console.terminal.MiraiConsoleTerminalLoader
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import org.junit.Before
import org.junit.Test
import top.colter.mirai.plugin.weibo.WeiboDynamicPlugin
import top.colter.mirai.plugin.weibo.client.WeiboClient
import top.colter.mirai.plugin.weibo.data.WeiboDynamic
import top.colter.mirai.plugin.weibo.data.WeiboEmoji
import top.colter.mirai.plugin.weibo.data.WeiboFullContent
import top.colter.mirai.plugin.weibo.draw.DynamicDraw
import top.colter.mirai.plugin.weibo.lisener.GroupMessageListener
import top.colter.mirai.plugin.weibo.tools.decode
import top.colter.mirai.plugin.weibo.tools.loadResourceBytes
import top.colter.mirai.plugin.weibo.tools.weiboClient
import top.colter.skiko.Dp
import top.colter.skiko.FontUtils
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


internal class DrawTest {

    private val client = WeiboClient()

    @OptIn(ConsoleExperimentalApi::class)
    @Before
    fun init() {
        Dp.factor = 1f
//        MiraiConsoleTerminalLoader.startAsDaemon()
//        WeiboDynamicPlugin.load()
//        WeiboDynamicPlugin.enable()
//        FontUtils.loadTypeface(loadTestResource("font", "HarmonyOS_Sans_SC_Medium.ttf").absolutePath)
//        FontUtils.loadEmojiTypeface(loadTestResource("font", "NotoColorEmoji.ttf").absolutePath)

    }

    @Test
    fun `test dynamic`(): Unit = runBlocking {
        val msgId = "OknQ23YC1"
        val dynamic = weiboClient.get<WeiboDynamic>("https://weibo.com/ajax/statuses/show?id=$msgId")
//        val dd = weiboClient.get<WeiboFullContent>("https://weibo.com/ajax/statuses/longtext?id=$msgId")
//        println(dynamic)
//        println(dd)
        DynamicDraw(dynamic)
    }

    @Test
    fun `test dynamic style1`(): Unit = runBlocking {

        val emoji = loadTestResource("","emoji.json").readText()

        println(emoji.decode<List<WeiboEmoji>>())


//        val face = loadTestImage("image", "avatar.jpg")
//        val pendant = loadTestImage("image", "pendant.png")
//        val ornament1 = loadTestImage("image", "ornament1.png")
//        val ornament2 = loadTestImage("image", "ornament2.png")
//
//        val cover = loadTestImage("image", "bg1.jpg")

    }


    @Test
    fun `test dynamic1`(): Unit = runBlocking {
        val msgId = "O267cpt6p"
//        val dynamic = weiboClient.get<WeiboDynamic>("https://weibo.com/ajax/statuses/show?id=$msgId")
        weiboClient.useHttpClient {
            val aa = it.get("https://weibo.com/6278171447/5007200426660163").setCookie()
//            println(it.cookies("https://weibo.com/6278171447/5007200426660163"))
            println(aa)
            aa.forEach {
                println("${it.name} ==> ${it.value}")
            }

        }
//        val dd = weiboClient.get("https://weibo.com/6278171447/5007200426660163")
//        weiboClient.cookies("http://0.0.0.0:8080/")
    }

    @Test
    fun testtt(): Unit = runBlocking {
        val rr = "#怎么让全国人民都知道我瘦了30多斤# http://t.cn/A6EpW8TS \u200B\u200B\u200B"
//        println(rr.)
    }
    
}
