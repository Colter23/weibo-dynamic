package top.colter.mirai.plugin.weibo

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.registerTo
import net.mamoe.mirai.utils.info
import org.jetbrains.skia.FontStyle
import top.colter.mirai.plugin.weibo.data.WeiboDynamic
import top.colter.mirai.plugin.weibo.draw.DynamicDraw
import top.colter.mirai.plugin.weibo.lisener.GroupMessageListener
import top.colter.mirai.plugin.weibo.tools.weiboClient
import top.colter.skiko.FontUtils
import top.colter.skiko.FontUtils.loadTypeface
import top.colter.skiko.FontUtils.matchFamily
import kotlin.io.path.createDirectory
import kotlin.io.path.exists
import kotlin.io.path.forEachDirectoryEntry
import kotlin.io.path.name


object WeiboDynamicPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "top.colter.weibo-dynamic",
        name = "Weibo Dynamic",
        version = "0.1.2",
    ) {
        author("Colter")
        dependsOn("xyz.cssxsh.mirai.plugin.mirai-skia-plugin", ">= 1.1.0")
    }
) {
    override fun onEnable() {
        logger.info { "Plugin loaded" }

        WeiboConfig.reload()

        launch {
            val fontFolderPath = dataFolderPath.resolve("font")
            if (!fontFolderPath.exists()) fontFolderPath.createDirectory()
            fontFolderPath.forEachDirectoryEntry {
                if (it.toFile().isFile) loadTypeface(it.toString(), it.name.split(".").first())
            }

            if (FontUtils.defaultFont == null) {
                val defaultList = listOf("HarmonyOS Sans SC", "LXGW WenKai", "Source Han Sans", "SimHei", "sans-serif")
                defaultList.forEach {
                    try {
                        loadTypeface(matchFamily(it).matchStyle(FontStyle.NORMAL)!!)
                        logger.info("加载默认字体 $it 成功")
                        return@forEach
                    } catch (_: Exception) {
                    }
                }
            }

//            val msgId = "O7q51ztgb"
//            val dynamic = weiboClient.get<WeiboDynamic>("https://weibo.com/ajax/statuses/show?id=$msgId")
//            DynamicDraw(dynamic)

            GroupMessageListener.registerTo(globalEventChannel())
        }

    }

    override fun onDisable() {
        WeiboConfig.save()
    }
}