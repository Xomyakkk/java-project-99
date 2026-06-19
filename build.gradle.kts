plugins {
	java
	id("org.springframework.boot") version "4.0.6"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.sonarqube") version "7.3.1.8318"
	id("io.freefair.lombok") version "8.12.1"
	id("io.sentry.jvm.gradle") version "6.12.0"
	checkstyle
	jacoco
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

 repositories {
    mavenCentral()
    gradlePluginPortal()
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.security:spring-security-crypto")
	implementation("io.jsonwebtoken:jjwt-api:0.12.3")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.4")
	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-test-autoconfigure")
	testImplementation("com.fasterxml.jackson.core:jackson-databind")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

sonar {
	properties {
		property("sonar.projectKey", "Xomyakkk_java-project-99")
		property("sonar.organization", "xomyakkk")
		property(
			"sonar.coverage.jacoco.xmlReportPaths",
			"${layout.buildDirectory.get()}/reports/jacoco/test/jacocoTestReport.xml"
		)
	}
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required.set(true)
		html.required.set(true)
		csv.required.set(false)
	}
}

tasks.sonar {
	dependsOn(tasks.jacocoTestReport)
}

dependencyLocking {
	lockAllConfigurations()
}

sentry {
	includeSourceContext.set(true)
	org.set("hexlet-e4")
	projectName.set("java")
	authToken.set(System.getenv("SENTRY_AUTH_TOKEN"))
}
