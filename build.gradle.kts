import java.util.Properties

// Cargar archivo de versión
val versionPropsFile = file("version.properties")
val versionProps = Properties()

versionProps.load(versionPropsFile.inputStream())

// Obtener la versión actual
val currentVersion = versionProps.getProperty("version") ?: "1.0.0"

// Usar la versión como propiedad del proyecto
version = currentVersion

// Tarea para incrementar versión
tasks.register("incrementVersion") {
    doLast {
        val parts = currentVersion.split(".").map { it.toInt() }.toMutableList()
        parts[2] = parts[2] + 1 // incrementar el número de "patch"
        val newVersion = parts.joinToString(".")
        versionProps.setProperty("version", newVersion)
        versionProps.store(versionPropsFile.outputStream(), null)
        println("Versión actualizada a $newVersion")
    }
}

val artifactId = "users"
group = "ar.com.intrale"

val projectRepo = "https://maven.pkg.github.com/intrale/repo"
val jetBrainsRepo = "https://packages.jetbrains.team/maven/p/ktls/maven"
val confluenceRepo = "https://packages.confluent.io/maven"

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.shadow)

    `maven-publish`   // permite publicar artefactos como .jar
    `java-library`    // opcional, útil si estás creando una librería reutilizable
}

application {
    mainClass.set("ar.com.intrale.UsersApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    maven {
        name = "github"
        url = uri(projectRepo)
        credentials {
            username = System.getenv("GITHUB_ACTOR") ?: project.findProperty("gpr.user") as String?
            password = System.getenv("GITHUB_TOKEN") ?: project.findProperty("gpr.key") as String?
        }
    }
    mavenCentral()
    gradlePluginPortal()
    maven(url = uri(jetBrainsRepo))
    maven (url = uri(confluenceRepo))
}

dependencies {

    // Backend
    implementation(libs.backend)

    implementation("io.ktor:ktor-serialization-gson-jvm")

    implementation(libs.ktor.server.rate.limiting)

    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation(libs.kotlin.test.junit)

    // AWS Lambdas
    implementation(libs.aws.lambda.java.events)
    implementation(libs.aws.lambda.java.log4j)

    // AWS Cognito
    implementation(libs.cognito.identity.provider)

    // Validations
    implementation(libs.konform)

    // Kodein
    implementation(libs.kodein.di.framework.ktor.server.jvm)

    // Faker
    implementation(libs.datafaker)

}

publishing {
    publications {
        create<MavenPublication>("mavenJar") {
            from(components["java"])
            groupId = "$group"
            artifactId = "$artifactId"
            version = "$version"
        }
    }
    repositories {
        maven {
            name = "github"
            url = uri(projectRepo)
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: project.findProperty("gpr.user") as String?
                password = System.getenv("GITHUB_TOKEN") ?: project.findProperty("gpr.key") as String?
            }
        }
    }
}

tasks.named("build") {
    finalizedBy("incrementVersion")
}
