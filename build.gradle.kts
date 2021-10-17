plugins {
    java
}

allprojects {
    group = "org.confeti"
}

configure(subprojects.filterNot(project(":web")::equals)) {
    apply(plugin = "java")

    repositories {
        mavenCentral()
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(16))
        }
    }

    tasks {
        test {
            useJUnitPlatform()
        }
    }
}

