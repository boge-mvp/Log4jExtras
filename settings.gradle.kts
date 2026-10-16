pluginManagement {

    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }

}

dependencyResolutionManagement {

    @Suppress("UnstableApiUsage")
    repositories {
        mavenLocal()
        maven { url = uri("https://maven.aliyun.com/repository/central") }
        mavenCentral()
    }

}

//mavenDependencyConfig {
//    skippingTest = true
//}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
//    id("com.maven-dependency-preloader") version "1.0"

}
rootProject.name = "Log4jExtras"