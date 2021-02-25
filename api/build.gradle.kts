plugins {
    id("org.springframework.boot") version "2.3.5.RELEASE"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    checkstyle
    id("org.sonarqube") version "3.1.1"
}

checkstyle {
    configFile = file("$rootDir/checkstyle.xml")
    toolVersion = "8.40"
}

sonarqube {
    properties {
        //property("sonar.exclusions", "**/*Test.java")
        property("sonar.login", "admin")
        property("sonar.password", "Password123!")
        property("sonar.exclusions", "src/test/**/*")
        property("sonar.issue.ignore.multicriteria", "e1")
        property("sonar.issue.ignore.multicriteria.e1.ruleKey", "squid:S1452")
        property("sonar.issue.ignore.multicriteria.e1.resourceKey", "**/*.java")
    }
}

buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:3.1.1")
    }
}

apply(plugin = "org.sonarqube")

version = "1.0-SNAPSHOT"

extra["cassandra-driver.version"] = "4.9.0"

dependencies {
    // Lombok
    val lombok = "org.projectlombok:lombok"
    compileOnly(lombok)
    annotationProcessor(lombok)
    testCompileOnly(lombok)
    testAnnotationProcessor(lombok)

    // Driver (overriding spring defaults)
    annotationProcessor("com.datastax.oss:java-driver-mapper-processor")
    testAnnotationProcessor("com.datastax.oss:java-driver-mapper-processor")

    implementation("com.datastax.oss:java-driver-core")
    implementation("com.datastax.oss:java-driver-query-builder")
    implementation("com.datastax.oss:java-driver-mapper-runtime")
    implementation("com.datastax.oss:native-protocol")

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Reactor
    implementation("io.projectreactor.addons:reactor-extra")

    // Annotations for better code documentation
    implementation("org.jetbrains:annotations:19.0.0")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.projectreactor:reactor-test")

    testImplementation("org.testcontainers:testcontainers:1.15.0")
    testImplementation("org.testcontainers:junit-jupiter:1.15.0")
    testImplementation("org.testcontainers:cassandra:1.15.0")
}
