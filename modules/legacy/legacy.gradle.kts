import de.undercouch.gradle.tasks.download.Download

plugins {
    id("de.undercouch.download") version "4.0.4"
}

dependencies {
    compileOnly(fileTree("libs") {
        include("*.jar")
    })
}

tasks {
    val downloadTask by registering(Download::class) {
        src("https://cdn.getbukkit.org/spigot/spigot-1.5.2-R1.1-SNAPSHOT.jar")
        dest(file("libs").apply {
            mkdirs()
        })
        overwrite(false)
    }
    compileJava {
        dependsOn(downloadTask)
    }
}