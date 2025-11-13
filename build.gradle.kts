import dev.detekt.gradle.Detekt

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ktor) apply false
    alias(libs.plugins.devDetekt)
}

detekt {
    toolVersion = libs.versions.detekt
    config.setFrom(file("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    source.setFrom(
        "composeApp/src/",
        "shared/src/"
    )
    autoCorrect = true
}

tasks.withType<Detekt>().configureEach {
    reports {
        checkstyle.required.set(true)
        html.required.set(true)
        sarif.required.set(true)
        markdown.required.set(true)
    }
}

dependencies {
    detektPlugins(libs.detekt.rules.ktlint.wrapper)
}
