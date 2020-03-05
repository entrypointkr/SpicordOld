rootProject.name = "spicord"
include("legacy")

rootProject.children.forEach { project ->
    project.apply {
        projectDir = file("modules/$name")
        buildFileName = "$name.gradle.kts"
        name = "${rootProject.name}-$name"
    }
}