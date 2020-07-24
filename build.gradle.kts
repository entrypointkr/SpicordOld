plugins {
    base
}

allprojects {
    group = "kr.entree"

    afterEvaluate {
        if (pluginManager.hasPlugin("java")) {
            val sourceSet = withConvention(JavaPluginConvention::class) { sourceSets["main"] }
            tasks.withType<JavaCompile> {
                options.apply {
                    encoding = "UTF-8"
                    targetCompatibility = "1.8"
                    sourceCompatibility = targetCompatibility
                }
            }
            val sourcesJar = tasks.register<Jar>("sourcesJar") {
                archiveClassifier.set("sources")
                from(provider { sourceSet.allSource })
            }
            tasks.getByName("assemble").dependsOn(sourcesJar)
        }
    }
}