plugins {
    id("java")
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
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
                mapOf("Main-Class" to "pdfsplitter.PDFMergeApplication")
        )
    }
}