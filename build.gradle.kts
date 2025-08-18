plugins {
    java
    // A recent, stable Spring Boot version.
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
    // Great for build-time bytecode enhancement for better JPA performance.
    id("org.hibernate.orm") version "6.5.2.Final"
}

group = "com.pos"
version = "0.0.1-SNAPSHOT"
description = "PoS project for Spring Boot"

java {
    toolchain {
        // Java 21 is a modern and solid Long-Term Support (LTS) choice.
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // --- CORE & WEB ---
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    // For input validation (e.g., @NotNull, @Size). Essential for a PoS.
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // Jackson is the default JSON processor, pulled in by the web starter.
    // Explicitly adding spring-boot-starter-json is not required.

    // --- DATA & PERSISTENCE ---
    // Includes Spring Data JPA, Hibernate, and Connection Pooling (HikariCP).
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // The H2 in-memory database is great for rapid development and testing.
    runtimeOnly("com.h2database:h2")

    // --- SECURITY ---
    implementation("org.springframework.boot:spring-boot-starter-security")
    // Integrates Spring Security with Thymeleaf for conditional UI rendering.
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")

    // --- API & AUTHENTICATION (JWT) ---
    // Modern, modular JWT support. This is a crucial update from the older, monolithic jjwt:0.9.1.
    val jwtVersion = "0.12.5"
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jwtVersion")
    implementation("io.jsonwebtoken:jjwt-api:$jwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jwtVersion") // Or jjwt-gson if you prefer
    
    // For JSON Schema validation, useful for ensuring data integrity from external sources.
    implementation("com.github.fge:json-schema-validator:2.2.6")

    // --- DEVELOPER EXPERIENCE ---
    // Enables live reload and other development-time conveniences.
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    // Reduces boilerplate code (getters, setters, etc.).
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // --- DTO MAPPING ---
    // A powerful code generator for mapping between DTOs and entities.
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    // **CRITICAL FIX**: The annotation processor is required to generate mapping implementations.
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

    // --- ANNOTATION PROCESSING ---
    // Generates metadata for your custom application.properties/yml.
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // --- TESTING ---
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Configure annotation processors for MapStruct
tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Amapstruct.defaultComponentModel=spring")
}

// --- BUILD & RUN ---
hibernate {
    enhancement {
        // Optimizes JPA entity performance by enhancing bytecode at build time.
        enableAssociationManagement = true
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}