package top.colter.mirai.plugin.weibo.tools

import io.ktor.client.call.*
import io.ktor.client.request.*
import org.jetbrains.skia.Image
import top.colter.mirai.plugin.weibo.WeiboDynamicPlugin
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.time.Instant
import kotlin.io.path.*


enum class CacheType(val path: String) {
    DRAW("draw"),
    DRAW_DYNAMIC("draw/dynamic"),
    DRAW_LIVE("draw/live"),
    DRAW_SEARCH("draw/search"),
    IMAGES("images"),
    EMOJI("emoji"),
    USER("user"),
    OTHER("other"),
    UNKNOWN(""),
}

@Target(AnnotationTarget.PROPERTY)
annotation class Cache (
    val type: CacheType = CacheType.UNKNOWN
)

val cachePath: Path by lazy {
    WeiboDynamicPlugin.dataFolderPath.resolve("cache")
}

fun CacheType.cachePath(): Path {
    return cachePath.resolve(path).apply {
        if (notExists()) createDirectories()
    }
}

fun CacheType.cacheFile(filePath: String): Path {
    val split = filePath.split("/")
    val path = split.dropLast(1).joinToString("/")
    val file = split.last()
    return cachePath().resolve(path).apply {
        if (notExists()) createDirectories()
    }.resolve(file)
}


/**
 * 递归查找文件
 */
fun Path.findFile(file: String): Path? {
    forEachDirectoryEntry {
        if (it.isDirectory()) {
            val path = it.findFile(file)
            if (path != null) return path
        } else {
            if (it.name == file) return it
        }
    }
    return null
}

fun cacheImage(image: Image?, path: String, cacheType: CacheType): Boolean {
    if (image == null) return false
    val file = cacheType.cacheFile(path)
    file.writeBytes(image.encodeToData()!!.bytes)
    return true
}


suspend fun getOrDownload(url: String, cacheType: CacheType = CacheType.UNKNOWN): ByteArray? {
    try {
        val fileName = url.split("?").first().split("@").first().split("/").last()

        val filePath = if (cacheType == CacheType.UNKNOWN) {
            cachePath.findFile(fileName) ?: CacheType.OTHER.cacheFile(fileName)
        } else {
            cacheType.cacheFile(fileName)
        }
        return if (filePath.exists()) {
            filePath.setLastModifiedTime(FileTime.from(Instant.now()))
            filePath.readBytes()
        } else {
            try {
                weiboClient.useHttpClient {
                    it.get(url).body<ByteArray>().apply {
                        filePath.writeBytes(this)
                    }
                }
            }catch (t: Throwable) {
                logger.error("下载图片失败! $url\n$t")
                return null
            }
        }
    }catch (t: Throwable) {
        logger.error("获取图片失败! $url\n$t")
        return null
    }
}

suspend fun getOrDownloadImage(url: String, cacheType: CacheType = CacheType.UNKNOWN) = try {
    getOrDownload(url, cacheType)?.let { Image.makeFromEncoded(it) }
}catch (t: Throwable){
    logger.error("解析图片失败! $url\n$t")
    null
}
