plugins {
	java
	id("org.springframework.boot") version "3.5.7"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "ru.webbyskytracker"
version = "0.0.1-SNAPSHOT"
description = "sender-to-email-service"

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
	//Lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	//Mail Sender
	implementation("org.springframework.boot:spring-boot-starter-mail")
	//Kafka
	implementation("org.springframework.kafka:spring-kafka")
	//Spring Cloud Config
	//implementation("org.springframework.cloud:spring-cloud-starter-config")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}