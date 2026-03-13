import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.gradle.api.publish.maven.MavenPublication

plugins {
    id("maven-publish")
}

val isUnobfuscatedBranch = sc.current.version.startsWith("26.1")
apply(plugin = if (isUnobfuscatedBranch) "net.fabricmc.fabric-loom" else "net.fabricmc.fabric-loom-remap")

val modVersion = property("mod.version") as String
val modGroup = property("mod.group") as String
val modName = property("mod.name") as String
val modId = property("mod.id") as String
val modArtifact = property("mod.artifact") as String
val minecraftDependency = property("mod.mc_dep") as String
val loaderVersion = property("deps.fabric_loader") as String
val fabricApiVersion = property("deps.fabric_api") as String
val loomExtension = extensions.getByType<LoomGradleExtensionAPI>()

version = modVersion
group = modGroup
base.archivesName = modArtifact

val licenseArchiveSuffix = base.archivesName.get()
val requiredJava = when {
    isUnobfuscatedBranch -> JavaVersion.toVersion(25)
    sc.current.parsed >= "1.20.5" -> JavaVersion.toVersion(21)
    sc.current.parsed >= "1.18" -> JavaVersion.toVersion(17)
    sc.current.parsed >= "1.17" -> JavaVersion.toVersion(16)
    else -> JavaVersion.toVersion(8)
}

dependencies {
    add("minecraft", "com.mojang:minecraft:${sc.current.version}")

    if (isUnobfuscatedBranch) {
        add("implementation", "net.fabricmc:fabric-loader:$loaderVersion")
        add("implementation", "net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
    } else {
        add("mappings", loomExtension.officialMojangMappings())
        add("modImplementation", "net.fabricmc:fabric-loader:$loaderVersion")
        add("modImplementation", "net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
    }
}

configure<LoomGradleExtensionAPI> {
    runConfigs.all {
        ideConfigGenerated(true)
        runDir = "../../run"
    }
}

java {
    withSourcesJar()
    sourceCompatibility = requiredJava
    targetCompatibility = requiredJava
    if (JavaVersion.current() < requiredJava) {
        toolchain.languageVersion = JavaLanguageVersion.of(requiredJava.majorVersion.toInt())
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = requiredJava.majorVersion.toInt()
}

tasks {
    processResources {
        inputs.property("id", modId)
        inputs.property("name", modName)
        inputs.property("version", modVersion)
        inputs.property("minecraft", minecraftDependency)
        inputs.property("loader", loaderVersion)

        val props = mapOf(
            "id" to modId,
            "name" to modName,
            "version" to modVersion,
            "minecraft" to minecraftDependency,
            "loader" to loaderVersion
        )

        filesMatching("fabric.mod.json") {
            expand(props)
        }

        val mixinJava = "JAVA_${requiredJava.majorVersion}"
        filesMatching("*.mixins.json") {
            expand("java" to mixinJava)
        }
    }

    jar {
        from(rootProject.file("LICENSE.txt")) {
            rename("LICENSE.txt", "LICENSE.txt_${licenseArchiveSuffix}")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = modArtifact
            from(components["java"])
        }
    }
}
