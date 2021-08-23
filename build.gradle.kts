/*
 * SPDX-License-Identifier: BSD-2-Clause
 * Copyright (c) 2021, anatawa12 and other contributors, All rights reserved.
 * This file is a part of jstack-gui which is under BSD 2-Clause "Simplified" License.
 */

plugins {
    id("java")
}

group = "com.anatawa12"
version = "1.0-SNAPSHOT"

val api by sourceSets.creating

repositories {
    mavenCentral()
}

dependencies {
    implementation(api.output)
    "apiImplementation"("org.jetbrains:annotations:21.0.1")
    "apiImplementation"("net.sf.jopt-simple:jopt-simple:5.0.4")
}

tasks.processResources {
    val javaHome = providers.systemProperty("java.home").forUseAtConfigurationTime()
    val toolsJar = javaHome.map { File("$it/lib/tools.jar").takeIf { it.exists() } }
        .orElse(javaHome.map { File("$it/../lib/tools.jar").takeIf { it.exists() } })
    from(toolsJar) {
        into("com/anatawa12/jstack/gui")
    }
    from("LICENSE")
}

tasks.jar {
    manifest.attributes(
        "TweakClass" to "com.anatawa12.jstack.gui.Tweaker",
        "MainClass" to "com.anatawa12.jstack.gui",
    )
}
