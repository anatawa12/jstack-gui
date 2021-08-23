/*
 * SPDX-License-Identifier: BSD-2-Clause
 * Copyright (c) 2021, anatawa12 and other contributors, All rights reserved.
 * This file is a part of jstack-gui which is under BSD 2-Clause "Simplified" License.
 */

plugins {
    id("java")
}

group = "com.anatawa12"
version = "1.0"

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
    from("LICENSE")
}

tasks.jar {
    manifest.attributes(
        "TweakClass" to "com.anatawa12.jstack.gui.Tweaker",
        "Main-Class" to "com.anatawa12.jstack.gui.Main",
    )
}
