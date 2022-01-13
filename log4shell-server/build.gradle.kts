import java.nio.charset.Charset

plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("info.picocli:picocli:4.6.2")
    implementation("com.unboundid:unboundid-ldapsdk:6.0.3")
    implementation(platform("io.netty:netty-bom:4.1.72.Final"))
    implementation("io.netty:netty-codec-http")
    implementation(platform("com.fasterxml.jackson:jackson-bom:2.13.1"))
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")

    implementation(platform("software.amazon.awssdk:bom:2.17.107"))
    implementation("software.amazon.awssdk:sts")

    implementation("org.slf4j:slf4j-api:1.7.32")
    runtimeOnly("ch.qos.logback:logback-classic:1.2.10")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

application {
    mainClass.set("log4shell.ShellServer")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
