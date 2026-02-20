plugins {
    java
    id("org.springframework.boot") version "3.5.10"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "ru.webbyskytracker.metricsservice"
version = "0.0.1-SNAPSHOT"
description = "metrics-service"

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
    //Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    //Web
    implementation("org.springframework.boot:spring-boot-starter-web")
    //Eureka
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    //Postgres
    runtimeOnly("org.postgresql:postgresql")
    //Hibernate
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    //Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    //Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")
    //Security
    implementation("org.springframework.boot:spring-boot-starter-security")
    //JWT
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
