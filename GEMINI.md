# GEMINI.md - EducationApp

## Project Overview
**EducationApp** is a Kotlin Multiplatform (KMP) project that shares both UI and business logic across Android and iOS platforms. It utilizes **Compose Multiplatform** for a unified UI development experience.

### Main Technologies
- **Kotlin Multiplatform (KMP)**: For sharing code between Android and iOS.
- **Compose Multiplatform**: For shared UI implementation.
- **Material 3**: For modern UI components and styling.
- **Android Gradle Plugin (AGP)**: Version 9.0.1.
- **Kotlin**: Version 2.3.21.

### Architecture
The project is organized into several modules:
- `:shared`: Contains the core logic and UI shared between platforms.
    - `commonMain`: Shared code, including the main `App()` composable.
    - `androidMain`: Android-specific implementations.
    - `iosMain`: iOS-specific implementations.
- `:androidApp`: The Android entry point, hosting the shared UI in a `MainActivity`.
- `iosApp`: A SwiftUI-based Xcode project that acts as the entry point for iOS.

## Building and Running

### Common Commands
- **Clean Project**: `./gradlew clean`
- **Build All**: `./gradlew build`

### Android
- **Assemble Debug APK**: `./gradlew :androidApp:assembleDebug`
- **Install and Run on Device**: `./gradlew :androidApp:installDebug`

### iOS
- **Open in Xcode**: Open the `iosApp/iosApp.xcodeproj` file in Xcode.
- **Build and Run**: Use the Run button in Xcode (requires a Mac with Xcode installed).

### Testing
- **Shared Common Tests**: `./gradlew :shared:allTests`
- **Android-specific Tests**: `./gradlew :shared:testAndroidHostTest`
- **iOS-specific Tests**: `./gradlew :shared:iosSimulatorArm64Test`

## Development Conventions

### Shared UI
- Most UI components should be defined in `shared/src/commonMain/kotlin`.
- Use `MaterialTheme` for styling to ensure consistency across platforms.
- Shared resources (images, strings) are managed via Compose Resources in `shared/src/commonMain/composeResources`.

### Platform-Specific Logic
- Use the `expect`/`actual` pattern for logic that requires platform-specific APIs.
- Define the `expect` declaration in `commonMain` and provide `actual` implementations in `androidMain` and `iosMain`.

### Dependencies
- Manage dependencies in `gradle/libs.versions.toml` to ensure version consistency across modules.
- Prefer multiplatform-compatible libraries (e.g., Ktor for networking, SQLDelight for database) when adding new features.

### Testing Practices
- Write unit tests in `shared/src/commonTest` for shared logic.
- Use platform-specific test directories (`androidHostTest`, `iosTest`) only when necessary to test platform-specific integrations.
