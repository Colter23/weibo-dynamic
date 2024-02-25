plugins {
    val kotlinVersion = "1.8.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.16.0"
}

group = "top.colter"
version = "0.1.0"

repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {

    implementation("io.ktor:ktor-client-okhttp:2.1.0")
    implementation("io.ktor:ktor-client-encoding:2.1.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.1.0")
    implementation("com.cronutils:cron-utils:9.2.0")

    implementation("com.google.zxing:javase:3.5.1")
    implementation("org.jetbrains.kotlinx:atomicfu:0.19.0")
    api("org.jetbrains.skiko:skiko-awt:0.7.71")

    implementation("top.colter.skiko:skiko-layout:0.0.1"){
        exclude("org.jetbrains.skiko:skiko-awt")
    }
//    implementation("org.jetbrains.skiko:skiko-awt-runtime-windows-x64:0.7.71")

//    implementation("xyz.cssxsh.mirai:mirai-skia-plugin:1.3.0") {
//        exclude("org.jetbrains.skiko:skiko-awt")
//    }
    testImplementation("net.mamoe:mirai-core-mock:2.16.0")

    testImplementation(kotlin("test", "1.8.0"))
    testImplementation("org.jetbrains.skiko:skiko-awt-runtime-windows-x64:0.7.71")
}


mirai {
    jvmTarget = JavaVersion.VERSION_11
}
