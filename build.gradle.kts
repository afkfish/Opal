plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.afkfish"
version = "0.3"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("org.javacord:javacord:3.7.0")
    implementation("com.github.Walkyst.lavaplayer-fork:lavaplayer:e833a69a10")

    implementation("org.apache.logging.log4j:log4j-api:2.19.0")
    implementation("org.apache.logging.log4j:log4j-core:2.19.0")
    implementation("org.apache.logging.log4j:log4j-slf4j18-impl:2.18.0")
}

tasks.jar {
    manifest {
        // define main class attribute
        attributes["Main-Class"] = "com.afkfish.Opal"
    }
}

// set build encoding
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}