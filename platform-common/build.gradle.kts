description = "banco-common"
base.archivesName.set("banco-common")

dependencies {
    api(project(":api"))
    compileOnly("org.spigotmc:spigot-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("ovh.mythmc:social-api:0.5.0db2")
    compileOnly("net.milkbowl.vault:VaultUnlockedAPI:2.11")
    compileOnly("net.tnemc:EconomyCore:0.1.4.0")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "banco-common"
            from(components["java"])
        }
    }
}
