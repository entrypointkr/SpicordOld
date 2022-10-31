import de.undercouch.gradle.tasks.download.Download
import kr.entree.spigradle.kotlin.codemc
import kr.entree.spigradle.kotlin.lombok
import java.util.jar.JarFile

plugins {
    java
    id("de.undercouch.download") version "4.0.4"
    id("kr.entree.spigradle") version "2.2.3"
}

val bukkitProject: Project get() = project(":spicord-bukkit")

version = bukkitProject.version

repositories {
    mavenCentral()
    codemc()
}

dependencies {
    implementation(bukkitProject)
    compileOnly(lombok())
    annotationProcessor(lombok())
    compileOnly(fileTree("libs") {
        include("*.jar")
    })
    compileOnly("org.jetbrains:annotations:19.0.0")
}

tasks {
    val downloadLegacySpigot by registering(Download::class) {
        src("https://cdn.getbukkit.org/spigot/spigot-1.5.2-R1.1-SNAPSHOT.jar")
        dest(file("$projectDir/libs/spigot.jar").apply {
            parentFile.mkdirs()
        })
        onlyIfModified(true)
        overwrite(true)
    }
    val extractJar by registering {
        dependsOn(bukkitProject.tasks["assemble"])
        doLast {
            val dest = sourceSets["main"].java.destinationDirectory.get().asFile
            JarFile(bukkitProject.tasks.getByName("shadowJar", Jar::class)
                    .archiveFile.get().asFile).use { jarFile ->
                for (entry in jarFile.entries()) {
                    if (entry.isDirectory || entry.name == "kr/entree/spicord/bukkit/util/Platform.class") continue
                    dest.resolve(entry.name).apply {
                        parentFile.mkdirs()
                        createNewFile()
                    }.outputStream().buffered().use { out ->
                        jarFile.getInputStream(entry).use { it.copyTo(out) }
                    }
                }
            }
        }
    }
    generateSpigotDescription {
        enabled = false
    }
    compileJava {
        dependsOn(downloadLegacySpigot, extractJar)
    }
    val copyLegacySpigot by registering(Copy::class) {
        dependsOn(downloadLegacySpigot)
        from("$projectDir/libs/spigot.jar") {
            rename { spigot.debug.serverJar.name }
        }
        into(spigot.debug.serverDirectory)
    }
    prepareSpigot {
        enabled = false
        dependsOn(copyLegacySpigot)
    }
    buildSpigot {
        enabled = false
    }
}