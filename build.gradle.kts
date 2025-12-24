plugins {
    java
    application
}

group = "com.profiluefter"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    // JDA Discord Bot Library
    implementation("net.dv8tion:JDA:6.2.0")
    
    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.12")
    implementation("org.slf4j:slf4j-api:2.0.16")
}

application {
    mainClass.set("com.profiluefter.vibebotv3.VibeBotv3")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.profiluefter.vibebotv3.VibeBotv3"
    }
    
    // Create a fat jar with all dependencies
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}
