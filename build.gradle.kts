plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("kr.entree.spigradle") version "1.1.4"
}

group = "kr.entree"
version = "1.1.7-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.14.4-R0.1-SNAPSHOT") {
        exclude(module = "bungeecord-chat")
    }
    compileOnly("org.projectlombok:lombok:1.18.10")
    implementation("net.dv8tion:JDA:4.0.0_61")
    implementation("club.minnced:discord-webhooks:0.1.8")
    implementation("org.ahocorasick:ahocorasick:0.4.0")
    implementation("org.bstats:bstats-bukkit:1.5")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("'com.fasterxml.jackson.core:jackson-core:2.10.1' // For legacy")
    annotationProcessor("org.projectlombok:lombok:1.18.10")
    testImplementation("org.mockito:mockito-core:3.1.0")
    testImplementation("junit:junit:4.12")
    testImplementation("org.spigotmc:spigot-api:1.14.4-R0.1-SNAPSHOT") {
        exclude(module = "bungeecord-chat")
    }
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
    build {
        dependsOn(shadowJar)
    }
    jar {
        enabled = false
    }
}