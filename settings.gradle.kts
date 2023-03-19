pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        maven {
            name = "parchment"
            url = uri("https://maven.parchmentmc.org")
        }
        maven {
            name = "architectury"
            url = uri("https://maven.architectury.dev/")
        }
        maven {
            name = "forge"
            url = uri("https://maven.minecraftforge.net/")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "fabricated-forge-api"

try {
    val path = rootDir
    val strings: List<String> = java.nio.file.Files.lines(File(path,"excluded-folders.txt").toPath()).toList()
    for (f in path.listFiles()!!) {
        if (f.isDirectory) {
            if (!strings.contains(f.name)) {
                val subprojects = File(f, "submodules.txt")
                if(subprojects.exists()){
                    val subStrings: List<String> = java.nio.file.Files.lines(subprojects.toPath()).toList()
                    for (s in subStrings){
                        val subproject = File(f, s)
                        if(subproject.exists()){
                            include(f.name + ":" + s)
                            project(":" + f.name + ":" + s).projectDir = subproject
                        }
                    }
                } else {
                    include(f.name)
                }
            }
        }
    }
} catch (e: Exception) {
    println("Submodule setup failed:")
    throw e
}
