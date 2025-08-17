plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "com.pos"
version = "0.0.1-SNAPSHOT"
description = "PoS project for Spring Boot"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(annotationProcessor)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Web & API Layer
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.rest)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.thymeleaf)

    // Data Layer
    implementation(libs.spring.boot.starter.data.jpa)
    runtimeOnly(libs.h2)

    // Security
    implementation(libs.spring.boot.starter.security)
    implementation(libs.thymeleaf.extras.springsecurity6)
    testImplementation(libs.spring.security.test)
    
    // Developer Tools
    developmentOnly(libs.spring.boot.devtools)
    developmentOnly(libs.spring.boot.docker.compose)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    annotationProcessor(libs.spring.boot.configuration.processor)
    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)

    // Testing
    testImplementation(libs.spring.boot.starter.test)
}

tasks.withType<Test> {
    useJUnitPlatform()
}