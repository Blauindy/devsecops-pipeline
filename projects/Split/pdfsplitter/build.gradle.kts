group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

plugins {
    id("java")
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.sonarqube") version "5.1.0.4882"
    id("org.owasp.dependencycheck") version "6.0.2"
}

sonar {
  properties {
    property("sonar.projectKey", "Blauindy_devsecops-pipeline")
    property("sonar.organization", "inf22")
    property("sonar.host.url", "https://sonarcloud.io")
  }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.apache.pdfbox:pdfbox:3.0.1")
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.2")
    implementation ("org.json:json:20240205")
    implementation("org.springframework.boot:spring-boot-starter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes(
                mapOf("Main-Class" to "pdfsplitter.PDFSplitApplication")
        )
    }
}

dependencyCheck {
    failBuildOnCVSS = 7.0f
    format = org.owasp.dependencycheck.reporting.ReportGenerator.Format.ALL
    outputDirectory = "${buildDir}/reports/dependency-check"
}