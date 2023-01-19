plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.afkfish"
version = "0.3"

repositories {
    mavenCentral()
    maven { setUrl("https://jitpack.io") }
}

dependencies {
    implementation("org.javacord:javacord:3.7.0")
    implementation("com.github.Walkyst.lavaplayer-fork:lava-common:custom-SNAPSHOT")
    implementation("com.github.Walkyst.lavaplayer-fork:lavaplayer:custom-SNAPSHOT")
    runtimeOnly("com.github.Walkyst.lavaplayer-fork:lavaplayer-ext-format-xm:custom-SNAPSHOT")
    runtimeOnly("com.github.Walkyst.lavaplayer-fork:lavaplayer-ext-youtube-rotator:custom-SNAPSHOT")
    runtimeOnly("com.github.Walkyst.lavaplayer-fork:lavaplayer-stream-merger:custom-SNAPSHOT")

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