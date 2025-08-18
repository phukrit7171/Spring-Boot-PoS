plugins {
    java
    // Use a recent, stable Spring Boot version. 3.3.3 is a good choice as of late 2025.
    id("org.springframework.boot") version "3.3.3" 
    id("io.spring.dependency-management") version "1.1.6"
    // The Hibernate plugin is great for build-time bytecode enhancement for better performance.
    id("org.hibernate.orm") version "6.5.2.Final" 
}

group = "com.pos"
version = "0.0.1-SNAPSHOT"
description = "PoS project for Spring Boot"

java {
    toolchain {
        // Java 21 is a modern and solid choice.
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
    // --- Core Web & UI ---
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    // --- Data & Persistence ---
    // Includes Spring Data, Hibernate, and Connection Pooling (HikariCP)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // The H2 in-memory database for rapid development.
    runtimeOnly("com.h2database:h2")

    // --- Security ---
    implementation("org.springframework.boot:spring-boot-starter-security")
    // Integrates Spring Security with Thymeleaf for conditional rendering in the UI.
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")

    // --- Developer Experience & Validation ---
    // For input validation (e.g., @NotNull, @Size). Essential for a PoS.
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // Essential for monitoring health, metrics, etc. A best practice.
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    // Reduces boilerplate code (getters, setters, constructors). Great for speed.
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    // Enables live reload and other development-time conveniences.
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    
    // --- Annotation Processing ---
    // Generates metadata for your custom application.properties.
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // --- Testing ---
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

hibernate {
    enhancement {
        // Good practice for optimizing JPA entity performance.
        enableAssociationManagement = true
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}