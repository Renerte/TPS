
import kotlin.repeat

plugins {
    id("java-library")
    id("com.gradleup.shadow") version "9.3.1"
    id("run-hytale")
}

group = findProperty("pluginGroup") as String? ?: "com.example"
version = findProperty("pluginVersion") as String? ?: "1.0.0"
description = findProperty("pluginDescription") as String? ?: "A Hytale plugin template"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    compileOnly(files("C:/Users/Patrick/AppData/Roaming/Hytale/install/release/package/game/latest/Server/HytaleServer.jar"))

    compileOnly(fileTree("libs") { include("*.jar") })

    implementation("org.jetbrains:annotations:24.1.0")
    implementation("com.google.code.gson:gson:2.11.0")


    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
    implementation("org.java-websocket:Java-WebSocket:1.5.6")


    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testCompileOnly("org.projectlombok:lombok:1.18.42")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.42")
}

artifacts {
    archives(file("C:/Users/Patrick/AppData/Roaming/Hytale/install/release/package/game/latest/Server/HytaleServer-sources.jar")) {
        classifier = "sources"
    }
}


runHytale {
    serverFolder = "file:///C:/Users/Patrick/AppData/Roaming/Hytale/install/release/package/game/latest/Server/"
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release = 25
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
        val props = mapOf(
            "group" to project.group,
            "version" to project.version,
            "description" to project.description
        )
        inputs.properties(props)

        filesMatching("manifest.json") {
            expand(props)
        }
    }

    shadowJar {
        archiveBaseName.set(rootProject.name)
        archiveClassifier.set("")

        relocate("com.google.gson", "de.shiirroo.libs.gson")

        minimize()
    }
    test {
        useJUnitPlatform()
    }

    build {
        dependsOn(shadowJar)
    }

}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}
