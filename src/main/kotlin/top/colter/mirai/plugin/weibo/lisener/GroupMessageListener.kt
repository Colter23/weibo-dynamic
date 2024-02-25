package top.colter.mirai.plugin.weibo.lisener

import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.event.EventHandler
import net.mamoe.mirai.event.SimpleListenerHost
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.message.data.toMessageChain
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import top.colter.mirai.plugin.weibo.WeiboConfig
import top.colter.mirai.plugin.weibo.data.WeiboDynamic
import top.colter.mirai.plugin.weibo.data.WeiboFullContent
import top.colter.mirai.plugin.weibo.draw.DynamicDraw
import top.colter.mirai.plugin.weibo.tools.logger
import top.colter.mirai.plugin.weibo.tools.weiboClient
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.coroutines.CoroutineContext

object GroupMessageListener: SimpleListenerHost() {

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        logger.error("MessageEventListener Exception: $exception")
    }

    val formatterOr = DateTimeFormatter.ofPattern("EEE LLL dd HH:mm:ss Z yyyy", Locale.US)
    val formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss")

    @EventHandler
    suspend fun GroupMessageEvent.onMessage() {

        val msg = message.filter { it !is At && it !is Image }.toMessageChain().content.trim()

        val msgId = matchingRegular(msg)
        if (msgId != null) {
            val dynamic = weiboClient.get<WeiboDynamic>("https://weibo.com/ajax/statuses/show?id=$msgId")
            val dynamicContent = weiboClient.get<WeiboFullContent>("https://weibo.com/ajax/statuses/longtext?id=$msgId")
            if (dynamicContent.data?.longTextContent?.isNotBlank() == true) {
                dynamic.content = dynamicContent.data?.longTextContent
            }
            val parsedDate = LocalDateTime.parse(dynamic.createdTime, formatterOr)
            dynamic.createdTime = formatter.format(parsedDate)

            val image = DynamicDraw(dynamic)
            if (image != null) {
                subject.sendImage(image.encodeToData()!!.bytes.toExternalResource().toAutoCloseable())
            }
        }

    }

}

private val regex: List<Regex> = WeiboConfig.linkResolveConfig.reg

fun matchingRegular(content: String): String? {
    return if (regex.any { it.find(content) != null }) {
        logger.info("开始解析链接 -> $content")
        return regex.first().find(content)?.destructured?.component1()
    } else null
}
