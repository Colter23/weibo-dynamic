package top.colter.mirai.plugin.weibo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeiboDynamic(
    @SerialName("id")
    var id: Long = 0L,
    @SerialName("mblogid")
    var mblogid: String = "",
    @SerialName("user")
    var user: WeiboUser? = null,
    @SerialName("created_at")
    var createdTime: String = "",
    @SerialName("text_raw")
    var content: String? = "",

    @SerialName("pic_ids")
    var pics: List<String>? = null,

    @SerialName("page_info")
    var pageInfo: PageInfo? = null,

    @SerialName("retweeted_status")
    var origin: WeiboDynamic? = null,
)

@Serializable
data class WeiboUrl(
    @SerialName("url_title")
    var urlTitle: String = "",
)


/**
 * @param type (5:视频  23:投票)
 * @param objectType (video  hudongvote)
 */
@Serializable
data class PageInfo(
    @SerialName("type")
    var type: Int = 0,
    @SerialName("page_id")
    var pageId: String = "",
    @SerialName("object_type")
    var objectType: String = "",
    @SerialName("oid")
    var oid: String = "",
    @SerialName("page_title")
    var pageTitle: String = "",
    @SerialName("page_pic")
    var pagePic: String = "",
)




