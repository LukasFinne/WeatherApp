# Weather App

This is a Kotlin Multiplatform project targeting Desktop (JVM).
## Development

### Project Dependencies
- **Kotlin Multiplatform** - For shared code across platforms
- **Compose Multiplatform** - For cross-platform UI
- **Ktor Client** - For HTTP networking
- **Kotlinx Serialization** - For JSON serialization
- **Kotlinx Coroutines** - For asynchronous programming
- **Detekt** - For static analysis
- **Weather API** - yr.no 
- **MAP API** - nominatim.openstreetmap.org

---

## Project Structure

```
weather/
├── build.gradle.kts                 # Root build configuration
├── settings.gradle.kts              # Project settings
├── gradle.properties               # Gradle properties
├── gradlew                         # Gradle wrapper (Unix/Linux)
├── gradlew.bat                     # Gradle wrapper (Windows)
├── gradle/                         # Gradle wrapper files
│   └── wrapper/
├── composeApp/                     # Desktop application module
│   ├── build.gradle.kts            # ComposeApp build configuration
│   └── src/
│       └── jvmMain/                # JVM-specific code
│           ├── kotlin/             # Kotlin source files
│           └── composeResources/   # Compose resources (images, etc.)
└── shared/                         # Shared business logic
    ├── build.gradle.kts            # Shared module build configuration
    └── src/
        ├── commonMain/             # Platform-agnostic code
        │   └── kotlin/             # Shared Kotlin code
        └── commonTest/             # Common tests
            └── kotlin/             # Test source files
```

### Module Descriptions

* **[/composeApp](./composeApp/src)** - Contains the Desktop (JVM) Compose Multiplatform application
    - **[jvmMain](./composeApp/src/jvmMain/kotlin)** - Desktop-specific code and UI components
    - **[composeResources](./composeApp/src/jvmMain/composeResources)** - Application resources like images and drawables

* **[/shared](./shared/src)** - Contains shared business logic and data models
    - **[commonMain](./shared/src/commonMain/kotlin)** - Platform-agnostic code (repositories, data models, etc.)
    - **[commonTest](./shared/src/commonTest/kotlin)** - Unit tests for shared code

## Building and Running

### Prerequisites

- JDK 17 or higher
- No additional setup required (Gradle wrapper handles everything)

### Build and Run Desktop Application

To build and run the desktop application, use one of the following methods:

#### Using IDE
Use the run configuration from the run widget in your IDE's toolbar.

#### Using Terminal

**On Linux/macOS:**
```bash
./gradlew :composeApp:run
```

**On Windows:**
```cmd
.\gradlew.bat :composeApp:run
```

### Build Standalone Application

To create a distributable package:

**On Linux/macOS:**
```bash
# Build all available formats
./gradlew :composeApp:packageDistributionForCurrentOS

# Or build specific format
./gradlew :composeApp:packageDeb      # Debian package
./gradlew :composeApp:packageMsi      # Windows installer
./gradlew :composeApp:packageDmg      # macOS disk image
```

**On Windows:**
```cmd
# Build all available formats
.\gradlew.bat :composeApp:packageDistributionForCurrentOS

# Or build specific format
.\gradlew.bat :composeApp:packageMsi  # Windows installer
```

### Running Tests

**On Linux/macOS:**
```bash
# Run all tests
./gradlew check
```

**On Windows:**
```cmd
# Run all tests
.\gradlew.bat check
```

### Clean Build

**On Linux/macOS:**
```bash
./gradlew clean build
```

**On Windows:**
```cmd
.\gradlew.bat clean build
```

## Static Analysis with Detekt

This project uses [Detekt](https://detekt.dev/) for static code analysis.

### Run Detekt Analysis

To run the static analysis checks across the entire project:

**On Linux/macOS:**
```bash
./gradlew detekt
```

**On Windows:**
```cmd
.\gradlew.bat detekt
```
