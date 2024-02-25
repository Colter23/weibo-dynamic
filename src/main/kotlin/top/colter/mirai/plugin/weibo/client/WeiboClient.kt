package top.colter.mirai.plugin.weibo.client

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import top.colter.mirai.plugin.weibo.tools.json


public open class WeiboClient(private val timeout: Long = 15_000L): AbstractKtorClient() {

//    public open val storage: BiliCookiesStorage = BiliCookiesStorage()

    override fun initClient(): HttpClient = HttpClient(OkHttp) {
        defaultRequest {
            header(HttpHeaders.Origin, "https://weibo.com")
            header(HttpHeaders.Referrer, "https://weibo.com")
            header(HttpHeaders.Cookie, "SUB=_2A25I3epIDeRhGeNH7FUW8SjLzziIHXVrk2OArDV8PUJbkNB-LWv2kW1NSpVbLaCkaNQr0bUiUA6ZoiwtwIY60lIq;")
        }
        install(HttpTimeout) {
            socketTimeoutMillis = timeout
            connectTimeoutMillis = timeout
            requestTimeoutMillis = null
        }
//        install(HttpCookies) {
//            storage = this@WeiboClient.storage
//        }
        expectSuccess = true
        Json { json }
        BrowserUserAgent()
        ContentEncoding()
    }

}


//public suspend inline fun <reified T> WeiboClient.getData(url: String, crossinline block: HttpRequestBuilder.() -> Unit = {}): T{
//    return getData<BiliCommonResult, T>(url, block)
//}


