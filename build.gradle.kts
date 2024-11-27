plugins {
	java
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.6"
	id("io.freefair.lombok") version "8.6"
	application
}

application {
	mainClass = "link.shorter.ShorterApplication"
}

group = "link"
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
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.liquibase:liquibase-core")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation("org.mapstruct:mapstruct:1.6.0.Beta2")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
	implementation("org.openapitools:jackson-databind-nullable:0.2.6")

	testImplementation("org.springframework.boot:spring-boot-testcontainers:3.3.4")
	testImplementation("org.testcontainers:testcontainers:1.20.2")
	testImplementation("org.testcontainers:junit-jupiter:1.20.2")
	testImplementation("org.testcontainers:postgresql:1.20.2")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
