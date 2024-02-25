package top.colter.mirai.plugin.weibo.client

public interface StatusCode {
    public val code: Int
    public val message: String

    public fun handleStatus()
}