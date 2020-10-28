import kr.entree.spigradle.kotlin.bStats
import kr.entree.spigradle.kotlin.codemc
import kr.entree.spigradle.kotlin.lombok
import kr.entree.spigradle.kotlin.spigot

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("kr.entree.spigradle") version "2.2.2"
}

version = "1.4.4"

repositories {
    codemc()
}

dependencies {
    val daggerVersion = "2.26"
    api(project(":spicord-core"))
    implementation("com.google.dagger:dagger:${daggerVersion}")
    implementation(bStats())
    compileOnly(spigot("1.15.2"))
    compileOnly(lombok())
    annotationProcessor(lombok())
    annotationProcessor("com.google.dagger:dagger-compiler:${daggerVersion}")
    testImplementation("junit:junit:4.12")
    testImplementation(spigot("1.15.2"))
    testImplementation("org.mockito:mockito-core:3.1.0")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.10")
}

spigot {
    name = "Spicord"
    authors = listOf("EntryPoint")
    commands {
        create("spicord") {
            aliases = listOf("sc")
        }
    }
    debug {
        buildVersion = "1.16.2"
    }
}

tasks {
    shadowJar {
        val prefix = "kr.entree.spicord.libs"
        listOf("net.", "club.", "com.", "gnu.", "natives.",
                "okhttp3.", "okio.", "org.", "tomp2p.", "io.vavr.").forEach {
            relocate(it, "$prefix.$it") {
                exclude("org.spigotmc.*")
                exclude("org.bukkit.*")
                exclude("org.jetbrains.*")
                exclude("org.apache.*")
            }
        }
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        archiveClassifier.set("")
        minimize()
    }
    jar {
        enabled = false
        dependsOn(shadowJar)
    }
}