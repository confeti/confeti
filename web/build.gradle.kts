plugins {
    id("org.siouan.frontend-jdk11") version "4.0.1"
}

frontend {
    nodeVersion.set("14.14.0")
    yarnEnabled.set(true)
    yarnVersion.set("1.22.5")
    cleanScript.set("run clean")
    assembleScript.set("run build")
    checkScript.set("run check")
}
