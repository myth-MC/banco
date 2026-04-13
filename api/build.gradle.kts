description = "banco-api"
base.archivesName.set("banco-api")

dependencies {
    implementation("de.exlll:configlib-yaml:4.5.0")
    compileOnly("org.spigotmc:spigot-api:1.21.4-R0.1-SNAPSHOT")
    
    compileOnly("com.github.LoneDev6:api-itemsadder:3.6.3-beta-14")
    compileOnly("xyz.xenondevs.nova:nova-api:0.16.2")
    compileOnly("io.lumine:Mythic-Dist:5.8.2")
    
    compileOnly("com.github.oraxen:oraxen:1.173.0") {
        exclude(group = "me.gabytm.util", module = "actions-spigot")
        exclude(group = "org.jetbrains", module = "annotations")
        exclude(group = "com.ticxo", module = "PlayerAnimator")
        exclude(group = "com.github.stefvanschie.inventoryframework", module = "IF")
        exclude(group = "io.th0rgal", module = "protectionlib")
        exclude(group = "dev.triumphteam", module = "triumph-gui")
        exclude(group = "org.bstats", module = "bstats-bukkit")
        exclude(group = "com.jeff-media", module = "custom-block-data")
        exclude(group = "com.jeff-media", module = "persistent-data-serializer")
        exclude(group = "com.jeff_media", module = "MorePersistentDataTypes")
        exclude(group = "gs.mclo", module = "java")
    }
    
    compileOnly("com.nexomc:nexo:0.10.0") {
        exclude(group = "net.byteflux", module = "libby-bukkit")
        exclude(group = "org.bstats", module = "bstats-bukkit")
    }
    
    compileOnly("org.postgresql:postgresql:42.7.9")
}

tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(tasks.javadoc)
}

tasks.javadoc {
    options {
        (this as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "banco-api"
            from(components["java"])
            artifact(tasks["javadocJar"])
        }
    }
}
