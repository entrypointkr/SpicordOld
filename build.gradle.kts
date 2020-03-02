import kr.entree.spigradle.kotlin.dependency.bStats
import kr.entree.spigradle.kotlin.dependency.spigot
import kr.entree.spigradle.kotlin.repository.codemc

plugins {
    java
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("kr.entree.spigradle") version "1.1.5"
}

group = "kr.entree"
version = "1.1.7-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    codemc()
}

dependencies {
    compileOnly(spigot("1.14.4")) {
        exclude(module = "bungeecord-chat")
        testImplementation(this)
    }
    compileOnly("org.projectlombok:lombok:1.18.10")
    implementation("net.dv8tion:JDA:4.0.0_61")
    implementation("club.minnced:discord-webhooks:0.1.8")
    implementation("org.ahocorasick:ahocorasick:0.4.0")
    implementation(bStats())
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.fasterxml.jackson.core:jackson-core:2.10.1")
    annotationProcessor("org.projectlombok:lombok:1.18.10")
    testImplementation("org.mockito:mockito-core:3.1.0")
    testImplementation("junit:junit:4.12")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.10")
}

spigot {
    authors = listOf("EntryPoint")
    commands {
        create("spicord") {
            aliases = listOf("sc")
        }
    }
}

tasks {
    withType<JavaCompile> {
        options.apply {
            encoding = "UTF-8"
            compilerArgs.addAll(listOf("-Xlint:unchecked", "-Xlint:deprecation"))
        }
    }
    shadowJar {
        val prefix = "kr.entree.spicord.libs"
        listOf("net.", "club.", "com.", "gnu.", "natives.",
                "okhttp3.", "okio.", "org.", "tomp2p.").forEach {
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
    val sourcesJar = create<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }
    build {
        dependsOn(shadowJar, sourcesJar)
    }
    jar {
        enabled = false
    }
}