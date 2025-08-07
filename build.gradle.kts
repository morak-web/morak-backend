plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"

    kotlin("plugin.jpa") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"

}

group = "com.xhae"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    runtimeOnly("com.mysql:mysql-connector-j")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    runtimeOnly("com.h2database:h2")
    // kotlinx.serialization (Kotlin 공식 JSON 직렬화)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Kotlin Logging (SLF4J 기반 간단 로깅)
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    implementation("org.slf4j:slf4j-api:2.0.13")

    implementation("software.amazon.awssdk:s3:2.25.27")        // 버전은 최신/안정화된 것으로
    implementation("software.amazon.awssdk:auth:2.25.27")
    implementation("software.amazon.awssdk:regions:2.25.27")

    implementation("io.awspring.cloud:spring-cloud-starter-aws-secrets-manager-config:3.0.2")

}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
