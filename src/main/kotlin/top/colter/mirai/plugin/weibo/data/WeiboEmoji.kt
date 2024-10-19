package top.colter.mirai.plugin.weibo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class WeiboEmoji(
    @SerialName("phrase")
    var phrase: String = "",
    @SerialName("url")
    var url: String = "",
)