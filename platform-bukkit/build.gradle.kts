plugins {
    id("com.github.johnrengelman.shadow")
}

description = "banco-platform-bukkit"
base.archivesName.set("banco-legacy")

dependencies {
    implementation(project(":platform-common"))
    compileOnly("org.spigotmc:spigot-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("net.kyori:adventure-platform-bukkit:4.4.1")
}

tasks.shadowJar {
    archiveClassifier.set("")
    
    relocate("org.incendo", "ovh.mythmc.banco.libs.org.incendo")
    relocate("de.exlll", "ovh.mythmc.banco.libs.de.exlll")
    relocate("org.snakeyaml", "ovh.mythmc.banco.libs.org.snakeyaml")
    relocate("org.bstats", "ovh.mythmc.banco.libs.org.bstats")
    relocate("com.j256", "ovh.mythmc.banco.libs.com.j256")
    relocate("io.leangen", "ovh.mythmc.banco.libs.io.leangen")
    relocate("org.apiguardian", "ovh.mythmc.banco.libs.org.apiguardian")
    relocate("org.checkerframework", "ovh.mythmc.banco.libs.org.checkerframework")
}
