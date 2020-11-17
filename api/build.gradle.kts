plugins {
    id("org.springframework.boot") version "2.3.5.RELEASE"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
}

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
