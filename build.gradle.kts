plugins {
    alias(libs.plugins.kotlin.jvm)
    kotlin("kapt") version libs.versions.kotlin
    `maven-publish`
}

group = "com.github.boge-mvp"
version = providers.gradleProperty("version").getOrElse("1.0.0")

kapt {
    // https://kotlinlang.org/docs/kapt.html#keep-java-compiler-s-annotation-processors
    keepJavacAnnotationProcessors = true
    // https://kotlinlang.org/docs/kapt.html#measure-the-number-of-files-generated-with-annotation-processors
    showProcessorStats = true
    // https://kotlinlang.org/docs/kapt.html#non-existent-type-correction
    correctErrorTypes = true

}

dependencies {
    implementation(kotlin("reflect"))
    implementation(libs.bundles.log4j)

    // 使用 log4j-core 处理源代码，其中 org.apache.logging.log4j.core.config.plugins.processor.PluginProcessor 会生成 Log4j2Plugins.dat 文件
    kapt(libs.log4j.core)

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(23)
}

// 确保Java组件可用以供发布
java {
    withSourcesJar()
    withJavadocJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(23))
    }
}

tasks.test {
    useJUnitPlatform()
}

// 配置发布到Maven仓库
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group.toString()
            artifactId = "Log4jExtras"
            version = version

            from(components["java"])

            pom {
                name.set("Log4jExtras")
                description.set("Additional features for Apache Log4j")
                url.set("https://github.com/boge-mvp/Log4jExtras")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("boge-mvp")
                        name.set("boge")
                        url.set("https://github.com/boge-mvp")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/boge-mvp/Log4jExtras")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}