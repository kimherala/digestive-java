plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta8"
}

group = "xyz.kimherala"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    //testImplementation(platform("org.junit:junit-bom:5.10.0"))
    //testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("commons-cli:commons-cli:1.9.0")
}

tasks.shadowJar {
    archiveClassifier.set("")  // optional: to avoid the default '-all' suffix
    archiveFileName.set("digestive.jar")
    manifest {
        attributes["Main-Class"] = "xyz.kimherala.digestive.Main"
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}


/*
tasks.test {
    useJUnitPlatform()
}
*/