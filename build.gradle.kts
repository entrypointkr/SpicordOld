import kr.entree.spigradle.kotlin.dependency.bStats
import kr.entree.spigradle.kotlin.dependency.spigot
import kr.entree.spigradle.kotlin.repository.codemc

plugins {
    java
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("kr.entree.spigradle") version "1.1.5"
}

dependencies {
    compileOnly(spigot("1.14.4")) {
        exclude(module = "bungeecord-chat")
        testImplementation(this)
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "kr.entree.spigradle")
    apply(plugin = "com.github.johnrengelman.shadow")

    group = "kr.entree"
    version = "1.1.7"

    repositories {
        mavenCentral()
        jcenter()
        codemc()
    }

    dependencies {
        val daggerVersion = "2.26"
        compileOnly("org.projectlombok:lombok:1.18.10")
        implementation("net.dv8tion:JDA:4.0.0_61")
        implementation("club.minnced:discord-webhooks:0.1.8")
        implementation("org.ahocorasick:ahocorasick:0.4.0")
        implementation(bStats())
        implementation("com.google.code.gson:gson:2.8.6")
        implementation("com.fasterxml.jackson.core:jackson-core:2.10.1")
        implementation("com.google.dagger:dagger:${daggerVersion}")
        annotationProcessor("org.projectlombok:lombok:1.18.10")
        annotationProcessor("com.google.dagger:dagger-compiler:${daggerVersion}")
        testImplementation("org.mockito:mockito-core:3.1.0")
        testImplementation("junit:junit:4.12")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.10")
    }

    tasks {
        withType<JavaCompile> {
            options.apply {
                encoding = "UTF-8"
                targetCompatibility = "1.8"
                sourceCompatibility = targetCompatibility
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
}

subprojects {
    val generatedSourceRoot = File(project.buildDir, "generated/sources/volatile/java/main")

    sourceSets {
        main {
            java {
                srcDir(generatedSourceRoot)
            }
        }
    }

    tasks {
        val copyClasses by registering(Copy::class) {
            from(rootProject.tasks.compileJava.get().source)
            into(generatedSourceRoot)
            exclude("**/kr/entree/spicord/bukkit/util/Platform*.java")
        }
        compileJava {
            dependsOn(copyClasses)
        }
        shadowJar {
            from(rootProject.tasks.spigotPluginYaml.get().temporaryDir)
        }
        spigotPluginYaml {
            enabled = false
        }
    }
}

spigot {
    name = buildString {
        project.name.apply {
            append(get(0).toUpperCase())
            append(substring(1))
        }
    }
    authors = listOf("EntryPoint")
    commands {
        create("spicord") {
            aliases = listOf("sc")
        }
    }
}