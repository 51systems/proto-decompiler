import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
    application
}

group = "com.51systems"
version = "0.1.0"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(platform("com.google.protobuf:protobuf-bom:3.14.0"))
    implementation("com.google.protobuf:protobuf-java")
    implementation("com.github.ajalt.clikt:clikt:3.1.0")
//    implementation("")

    testImplementation(kotlin("test-junit5"))
    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClassName = "MainKt"
}
