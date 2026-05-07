plugins {
    `java-library`
    id("com.gradleup.shadow") version "9.3.1" apply false
}

allprojects {
    group = "ovh.mythmc"
    version = "1.3.3"

    repositories {
        mavenCentral()
        maven("https://repo.mythmc.ovh/releases/") { name = "myth-mc-releases" }
        maven("https://repo.mythmc.ovh/snapshots/") { name = "myth-mc-snapshots" }
        maven("https://jitpack.io") { name = "jitpack" }
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") { name = "spigot-repo" }
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") { name = "placeholderapi" }
        maven("https://repo.xenondevs.xyz/releases") { name = "xenondevs" }
        maven("https://mvn.lumine.io/repository/maven-public/") { name = "nexus" }
        maven("https://repo.nexomc.com/releases") { name = "nexo" }
        maven("https://repo.papermc.io/repository/maven-public/") { name = "paper-repo" }
        maven("https://repo.codemc.org/repository/maven-public") { name = "codemc-repo" }
    }
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }
    
    configure<PublishingExtension> {
        repositories {
            maven {
                name = "mythMcReleases"
                url = uri(if (version.toString().contains("-db")) 
                    "https://repo.mythmc.ovh/snapshots/" 
                else 
                    "https://repo.mythmc.ovh/releases/")
                
                credentials {
                    username = project.findProperty("mythMcUser") as String? ?: System.getenv("MYTHMC_USER")
                    password = project.findProperty("mythMcPassword") as String? ?: System.getenv("MYTHMC_PASSWORD")
                }
            }
        }
    }

    dependencies {
        // Provided / compileOnly (from root pom)
        compileOnly("org.projectlombok:lombok:1.18.34")
        annotationProcessor("org.projectlombok:lombok:1.18.34")

        compileOnly("org.jetbrains:annotations:24.1.0")
        compileOnly("net.kyori:adventure-api:4.25.0")
        compileOnly("net.kyori:adventure-text-minimessage:4.25.0")
        compileOnly("net.kyori:adventure-text-serializer-legacy:4.25.0")
        
        compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
            exclude(group = "org.bukkit", module = "bukkit")
        }
        
        compileOnly("me.clip:placeholderapi:2.11.6")
        
        compileOnly("ovh.mythmc:callbacks-lib:0.1.2")
        annotationProcessor("ovh.mythmc:callbacks-lib:0.1.2")
        
        compileOnly("ovh.mythmc:gestalt-api:0.3.2")

        // Compile / implementation (from root pom)
        implementation("org.bstats:bstats-bukkit:3.0.2")
        implementation("ovh.mythmc:gestalt-loader:0.3.2")
        implementation("com.j256.ormlite:ormlite-core:6.1")
        implementation("com.j256.ormlite:ormlite-jdbc:6.1")
        implementation("org.incendo:cloud-paper:2.0.0-beta.15")
        implementation("org.incendo:cloud-minecraft-extras:2.0.0-beta.15")
    }
    
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(21)
    }

    tasks.processResources {
        filteringCharset = "UTF-8"
        val props = mapOf(
            "version" to version,
            "project" to mapOf("version" to version)
        )
        inputs.properties(props)
        filesMatching(listOf("*.yml", "paper-plugin.yml", "plugin.yml")) {
            expand(props)
        }
    }
}
