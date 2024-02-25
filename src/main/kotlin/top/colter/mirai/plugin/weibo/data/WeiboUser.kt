package top.colter.mirai.plugin.weibo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * @param verifiedType 认证 (-1:无认证  0:个人认证  7:企业认证)
 */
@Serializable
data class WeiboUser(
    @SerialName("id")
    var id: Long = 0L,
    @SerialName("screen_name")
    var name: String = "",
    @SerialName("avatar_large")
    var avatar: String = "",
    @SerialName("verified_type")
    var verifiedType: Int = -1,
)