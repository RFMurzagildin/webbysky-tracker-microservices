plugins {
    java
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "ru.webbyskytracker.aiservice"
version = "0.0.1-SNAPSHOT"
description = "ai-service"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2025.0.1"

dependencies {
    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    // Web
    implementation("org.springframework.boot:spring-boot-starter-web")
    // Eureka
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    // Postgres
    runtimeOnly("org.postgresql:postgresql")
    // Hibernate / JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // Security
    implementation("org.springframework.boot:spring-boot-starter-security")
    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
