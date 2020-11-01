import kr.entree.spigradle.kotlin.lombok

plugins {
    `java-library`
    id("kr.entree.spigradle.base") version "2.2.3"
}

dependencies {
    api("commons-lang:commons-lang:2.6")
    api("net.dv8tion:JDA:4.2.0_212")
    api("io.vavr:vavr:0.10.2")
    api("club.minnced:discord-webhooks:0.5.0")
    implementation("org.ahocorasick:ahocorasick:0.4.0")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.fasterxml.jackson.core:jackson-core:2.10.1")
    compileOnly(lombok())
    annotationProcessor(lombok())
    testImplementation("junit:junit:4.12")
    testImplementation("org.mockito:mockito-core:3.1.0")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.10")
}