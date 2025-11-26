plugins {
	java
	id("org.springframework.boot") version "3.5.7"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "ru.webbyskytracker"
version = "0.0.1-SNAPSHOT"
description = "users-service"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "2025.0.0"

dependencies {
	//Web
	implementation("org.springframework.boot:spring-boot-starter-web")
	//Eureka
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
	//Security
	//implementation("org.springframework.boot:spring-boot-starter-security")
	//Postgres
	runtimeOnly("org.postgresql:postgresql")
	//Hibernate
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	//Lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	//JWT
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
	//Redis
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	//Mail Sender
	implementation("org.springframework.boot:spring-boot-starter-mail")
	//Validation
	implementation("org.springframework.boot:spring-boot-starter-validation")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}