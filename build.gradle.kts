val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.0.20"
    id("io.ktor.plugin") version "2.3.12"
    id("com.github.johnrengelman.shadow") version "8.1.1"

    `maven-publish`   // permite publicar artefactos como .jar
    `java-library`    // opcional, útil si estás creando una librería reutilizable
}

group = "ar.com.intrale"
version = "0.0.2"

val kodeinVersion = "7.22.0"
val canardVersion = "1.2.0"
val konformVersion = "0.6.1"

application {
    mainClass.set("ar.com.intrale.UsersApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    maven {
        name = "github"
        url = uri("https://maven.pkg.github.com/intrale/repo")
        credentials {
            username = System.getenv("GITHUB_ACTOR") ?: project.findProperty("gpr.user") as String?
            password = System.getenv("GITHUB_TOKEN") ?: project.findProperty("gpr.key") as String?
        }
    }
    //maven(url = uri("https://maven.pkg.github.com/intrale/repo"))
    mavenCentral()
    //Kotless repository
    gradlePluginPortal()
    maven(url = uri("https://packages.jetbrains.team/maven/p/ktls/maven"))
    maven {
        url = uri("https://packages.confluent.io/maven")
        name = "confluence"
    }
}

dependencies {

    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-gson-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.github.flaxoos:ktor-server-rate-limiting:1.2.10")
    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")

    //

    // AWS Lambdas
    implementation(libs.aws.lambda.java.core)
    implementation(libs.aws.lambda.java.events)
    implementation(libs.aws.lambda.java.log4j)


    implementation("aws.sdk.kotlin:cognitoidentityprovider:1.2.28")
    implementation("aws.sdk.kotlin:cognitoidentity:1.2.28")
    implementation("aws.sdk.kotlin:secretsmanager:1.2.28")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // Validations
    implementation("io.konform:konform:$konformVersion")

    // Kodein
    implementation("org.kodein.di:kodein-di:$kodeinVersion")
    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:$kodeinVersion")

    // Faker
    implementation("net.datafaker:datafaker:2.4.2")

    // Backend Intrale
    implementation("ar.com.intrale:backend:0.0.6")
}

publishing {
    publications {
        create<MavenPublication>("mavenJar") {
            from(components["java"])
            groupId = "ar.com.intrale"
            artifactId = "users"
            version = "0.0.2"
        }
    }
    repositories {
        maven {
            name = "github"
            url = uri("https://maven.pkg.github.com/intrale/repo")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: project.findProperty("gpr.user") as String?
                password = System.getenv("GITHUB_TOKEN") ?: project.findProperty("gpr.key") as String?
            }
        }
    }
}
