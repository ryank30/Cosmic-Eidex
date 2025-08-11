import org.gradle.external.javadoc.StandardJavadocDocletOptions
import java.io.File
plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.0.14"
}

group = "app"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":shared"))
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.18.0")
    testImplementation("org.testfx:testfx-junit5:4.0.16-alpha")
    implementation("org.openjfx:javafx-controls:20.0.1")
    implementation("org.openjfx:javafx-fxml:20.0.1")
    implementation("com.google.code.gson:gson:2.10.1")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("app.Client")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(22))
    }
}

javafx {
    version = "20.0.1"
    modules("javafx.controls", "javafx.fxml")
}

tasks.withType<Javadoc>().configureEach {
    (options as StandardJavadocDocletOptions).apply {
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}