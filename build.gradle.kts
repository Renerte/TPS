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
    compileOnly(files("libs/HytaleServer.jar"))

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains:annotations:24.1.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

artifacts {
    archives(file("W:/Coding/Hytale/Server/2026.01.13-dcad8778f/Server/HytaleServer-sources.jar")) {
        classifier = "sources"
    }
}


runHytale {
    jarUrl = "file:///W:/Coding/Hytale/Server/2026.01.13-dcad8778f/Server/HytaleServer.jar"
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

        relocate("com.google.gson", "de.shiirrooo.libs.gson")

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
