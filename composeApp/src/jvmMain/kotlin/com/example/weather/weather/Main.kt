package com.example.weather.weather

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.awt.Dimension

/**
 * The height of the application window.
 */
const val HEIGHT = 400

/**
 * The width of the application window.
 */
const val WIDTH = 500

/**
 * The main entry point of the weather application.
 *
 * This function sets up and runs the Compose for Desktop application window.
 */
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "weather",
    ) {
        window.minimumSize = Dimension(WIDTH, HEIGHT)
        App()
    }
}
