plugins {
    java
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.7"

    // Great for build-time bytecode enhancement for better JPA performance.
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

repositories {
    mavenCentral()
}

dependencies {
    // --- CORE & WEB ---
    implementation("org.springframework.boot:spring-boot-starter-web")

    // For input validation (e.g., @NotNull, @Size). Essential for a PoS.
    implementation("org.springframework.boot:spring-boot-starter-validation")

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

    // Required for MapStruct to generate code at compile time.
    compileOnly("org.mapstruct:mapstruct-processor:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
    
    // If you want to use MapStruct with Lombok.
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")


    tasks.withType<JavaCompile> {
        options.compilerArgs.addAll(listOf(
            "-Amapstruct.defaultComponentModel=spring"
        ))
    }
}