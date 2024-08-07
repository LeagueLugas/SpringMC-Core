import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("jvm") version "2.0.20-Beta2"
    id("com.vanniktech.maven.publish") version "0.29.0"
}

group = "io.github.leaguelugas"
version = "0.8.0"
description = "A Framework for developing Minecraft plugin"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
        name = "spigotmc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(kotlin("reflect"))
}

kotlin {
    jvmToolchain(21)
}

tasks.jar {
    archiveFileName = "${project.name}-${project.version}.jar"
}

mavenPublishing {
    coordinates(
        groupId = project.group.toString(),
        artifactId = project.name,
        version = project.version.toString(),
    )
    pom {
        name.set("SpringMC-Core")
        description.set(project.description)
        inceptionYear.set("2024")
        url.set("https://github.com/leaguelugas/SpringMC-Core")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("leaguelugas")
                name.set("LeagueLugas")
                email.set("leaguelugas@gmail.com")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/LeagueLugas/SpringMC-Core.git")
            developerConnection.set("scm:git:ssh://github.com/LeagueLugas/SpringMC-Core.git")
            url.set("https://github.com/LeagueLugas/SpringMC-Core")
        }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}
