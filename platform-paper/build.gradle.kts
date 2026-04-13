plugins {
    id("com.gradleup.shadow")
}

description = "banco-platform-paper"
base.archivesName.set("banco-modern")

dependencies {
    implementation(project(":platform-common"))
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "banco-platform-paper"
            artifact(tasks.shadowJar)
        }
    }
}
