plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    //compileOnly("net.portswigger.burp.extensions:montoya-api:2025.2")
    implementation("com.intellij:forms_rt:7.0.3")
    implementation("net.portswigger.burp.extensions:montoya-api:2024.7")
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "21"
    targetCompatibility = "21"
    options.encoding = "UTF-8"
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().filter { it.isDirectory })
    from(configurations.runtimeClasspath.get().filterNot { it.isDirectory }.map { zipTree(it) })
}