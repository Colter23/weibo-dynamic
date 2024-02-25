package top.colter.mirai.plugin.weibo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class WeiboFullContent(
    @SerialName("data")
    var data: WeiboFullContentData? = null,
)

@Serializable
data class WeiboFullContentData(
    @SerialName("longTextContent")
    var longTextContent: String? = null,
)

