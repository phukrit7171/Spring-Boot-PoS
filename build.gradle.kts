plugins {
    java
    // A recent, stable Spring Boot version.
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.7"
    // Great for build-time bytecode enhancement for better JPA performance.
    id("org.hibernate.orm") version "7.1.0.Final"
    id("io.freefair.lombok") version "8.14.2"
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
    // For input validation (e.g., @NotNull, @Size). Essential for a PoS.
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // Jackson is the default JSON processor, pulled in by the web starter.
    // Explicitly adding spring-boot-starter-json is not required.
    implementation("org.springframework.boot:spring-boot-starter-json")

    // --- DATA & PERSISTENCE ---
    // Includes Spring Data JPA, Hibernate, and Connection Pooling (HikariCP).
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // The H2 in-memory database is great for rapid development and testing.
    runtimeOnly("com.h2database:h2")

    // --- SECURITY ---
    implementation("org.springframework.boot:spring-boot-starter-security")

    

    // --- DEVELOPER EXPERIENCE ---
    // Enables live reload and other development-time conveniences.
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    // Reduces boilerplate code (getters, setters, etc.).
	compileOnly("org.projectlombok:lombok:1.18.38")
	annotationProcessor("org.projectlombok:lombok:1.18.38")

    // --- DTO MAPPING ---
    // A powerful code generator for mapping between DTOs and entities.
    implementation("org.mapstruct:mapstruct:1.6.3")
    
    // **CRITICAL FIX**: The annotation processor is required to generate mapping implementations.
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

    // Ensure Lombok runs before MapStruct
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0") 

    // --- ANNOTATION PROCESSING ---
    // Generates metadata for your custom application.properties/yml.
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // --- TESTING ---
    testCompileOnly("org.projectlombok:lombok:1.18.38")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.38")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    runtimeOnly("org.springframework.boot:spring-boot-docker-compose")
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
