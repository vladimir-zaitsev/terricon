import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
}

group = "ru.ya.vsz"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    mavenCentral()
    maven("https://plugins.gradle.org/m2/")
}

dependencies {
    implementation("it.unimi.dsi:fastutil:6.4.4")
    implementation("net.sf.supercsv:super-csv:2.4.0")
    implementation("com.squareup:javapoet:1.12.1")
    implementation("org.apache.logging.log4j:log4j-core:2.12.4")
    implementation("org.json:json:20231013")
    implementation("com.google.guava:guava:32.0.1")

    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib-jdk8"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("MainKt")
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "17"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "17"
}