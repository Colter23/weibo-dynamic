package top.colter.mirai.plugin.weibo

import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value


object WeiboConfig : AutoSavePluginConfig("WeiboConfig") {

    @ValueDescription(
        """
        图片配置:
          defaultColor: 默认绘图主题色 支持多个值自定义渐变 中间用分号`;`号分隔 单个值会自动生成渐变色
          factor: 图片倍率 小数 默认1倍 1000px
    """
    )
    val imageConfig: ImageConfig by value()

    @ValueDescription(
        """
        链接解析配置:
          regex: 正则列表
    """
    )
    val linkResolveConfig: LinkResolveConfig by value()

}

@Serializable
data class ImageConfig(
    var defaultColor: String = "#BFCAFF;#BFFFF4",
    var factor: Float = 1f
)

@Serializable
data class LinkResolveConfig(
    val regex: List<String> = listOf(
        """https?://weibo\.com/\d+/([^?]+)"""
    )
){
    val reg: List<Regex> get() = regex.map { it.toRegex() }
}
