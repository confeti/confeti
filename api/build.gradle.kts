plugins {
    id("org.springframework.boot") version "2.3.4.RELEASE"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
}

version = "1.0-SNAPSHOT"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.jetbrains:annotations:19.0.0")

    // Driver (overriding spring defaults)
    implementation("com.datastax.oss:java-driver-core:4.9.0")
    implementation("com.datastax.oss:java-driver-query-builder:4.9.0")
    implementation("com.datastax.oss:java-driver-mapper-runtime:4.9.0")
    implementation("com.datastax.oss:native-protocol:1.4.11")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.springframework.security:spring-security-test")

    val lombok = "org.projectlombok:lombok:1.18.16"
    compileOnly(lombok)
    annotationProcessor(lombok)
    testCompileOnly(lombok)
    testAnnotationProcessor(lombok)
}
