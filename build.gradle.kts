plugins {
    java
}

allprojects {
    group = "org.confeti"
}

configure(subprojects.filterNot(project(":web")::equals)) {
    apply(plugin = "java")

    repositories {
        jcenter()
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_15
    }

    tasks {
        test {
            useJUnitPlatform()
        }
    }

    tasks.withType<JavaCompile> {
        options.compilerArgs.addAll(listOf("--enable-preview", "-Xlint:preview"))
    }
    tasks.withType<Test> {
        jvmArgs("--enable-preview")
    }
    tasks.withType<JavaExec> {
        jvmArgs("--enable-preview")
    }
}
