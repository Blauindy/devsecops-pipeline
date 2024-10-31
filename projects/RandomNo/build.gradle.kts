plugins {
    id("java")
    id("org.sonarqube") version "5.1.0.4882"
}

sonar {
  properties {
    property("sonar.projectKey", "Blauindy_devsecops-pipeline")
    property("sonar.organization", "inf22")
    property("sonar.host.url", "https://sonarcloud.io")
  }
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
