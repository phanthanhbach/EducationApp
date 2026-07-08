# EducationApp - Kotlin Multiplatform Education Portal

EducationApp is a modern, cross-platform mobile application built using **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**. It provides a comprehensive school portal tailored for three user roles: **Students**, **Teachers**, and **Parents**.

The project shares UI components, state management, networking, data persistence, and business logic across both **Android** and **iOS** platforms.

---

## 🌟 Key Features by User Role

### 👨‍🎓 Student Role
*   **Dashboard**: View daily statistics, announcement notifications, and upcoming sessions.
*   **Schedule**: Access a calendar-based class timetable.
*   **My Classes**: View enrolled classes, list of classmates, assignments, attendance logs, and class feedback.
*   **Payments**: View invoices, track payment status, and review historical transactions.
*   **Profile**: Manage personal information and customize settings (Theme, Language).

### 👩‍🏫 Teacher Role
*   **Dashboard**: Monitor active classes, quick stats, and pending assignments.
*   **Schedule**: Access teaching schedule and class timings.
*   **Classes**:
    *   Manage student lists and parent contacts.
    *   Take daily attendance.
    *   Create, view, and grade assignments.
    *   Write academic and behavioral feedback for individual students.
*   **Profile**: View teacher profile and adjust app preferences.

### 👪 Parent Role
*   **My Children**: Track multiple children's academic profiles, active classes, schedules, and attendance.
*   **Feedback**: Read teacher reports and comments for each child.
*   **Payments**: Pay tuition fees, view billing invoices, and download receipts.
*   **Profile**: Manage parent account and system preferences.

---

## 📱 Screenshots / Giao diện ứng dụng

Dưới đây là một số hình ảnh giao diện thực tế của ứng dụng trên cả hai nền tảng:

| Màn hình đăng nhập | Trang chủ (Học sinh) | Thời khóa biểu |
| :---: | :---: | :---: |
| <img src="docs/screenshots/login.png" width="220" alt="Login Screen"/> | <img src="docs/screenshots/student_dashboard.png" width="220" alt="Student Dashboard"/> | <img src="docs/screenshots/schedule.png" width="220" alt="Schedule Screen"/> |

| Danh sách lớp học | Nhận xét / Góp ý | Hóa đơn & Học phí |
| :---: | :---: | :---: |
| <img src="docs/screenshots/classes.png" width="220" alt="Classes List"/> | <img src="docs/screenshots/feedback.png" width="220" alt="Feedback & Comments"/> | <img src="docs/screenshots/payments.png" width="220" alt="Payments & Invoices"/> |

---

## 🛠 Tech Stack & Architecture

This application employs a clean architecture with clear separation of concerns (Presentation, Domain, Data) and features modern Kotlin/Compose libraries:

*   **UI Framework**: Compose Multiplatform & Material 3 for shared native UI widgets.
*   **Styling & Effects**: [Haze](https://github.com/chrisbanes/haze) for premium backdrop blur and glassmorphism styling.
*   **Navigation**: [Voyager](https://github.com/adriel/voyager) (TabNavigator, ScreenModel, and Transitions).
*   **Dependency Injection**: [Koin](https://insert-koin.io/).
*   **Networking**: [Ktor Client](https://ktor.io/) for API communication with Content Negotiation, Auth tokens, and Logging.
*   **Serialization**: `kotlinx.serialization` for JSON parsing.
*   **Local Storage**: [Multiplatform Settings](https://github.com/russhwolf/multiplatform-settings) for secure storage of session tokens, and DataStore Preferences.
*   **Image Loading**: [Coil 3](https://github.com/coil-kt/coil) with Ktor network engine.
*   **Date & Time**: `kotlinx-datetime` for localized calendar operations.
*   **Localization**: Multi-language support (English and Vietnamese) built with Compose resources.
*   **Image Editing**: [UCrop](https://github.com/Yalantis/uCrop) integration for profile photo cropping.
*   **Logging**: [Kermit](https://github.com/touchlab/Kermit) for multiplatform logging.

---

## 📂 Project Structure

```
├── androidApp/          # Android host application (Manifest, launcher assets, application class)
├── iosApp/              # iOS SwiftUI host application (Xcode project, AppDelegate, entry point)
└── shared/              # Core multiplatform code
    └── src/
        ├── commonMain/  # Shared Compose UI, Domain Models, Repository Interfaces, Koin DI, etc.
        │   └── composeResources/  # Locales, Drawables, Fonts (multi-language string resources)
        ├── androidMain/ # Android-specific APIs (Secure Storage Settings, DataStore Factory)
        └── iosMain/     # iOS-specific actual implementations
```

---

## 🚀 Getting Started

### Prerequisites
*   **Android Studio** (Koala or newer) or **IntelliJ IDEA** with the Kotlin Multiplatform plugin.
*   **macOS** with **Xcode** (v15+) if you want to run the iOS application.
*   **JDK 17+** configured in your development environment.

### Gradle CLI Commands

You can build, run, and test the project using the Gradle wrapper (`gradlew` on Unix/macOS or `gradlew.bat` on Windows):

| Task / Command | Description |
|---|---|
| `.\gradlew.bat build` | Runs compilation and validation checks for all targets |
| `.\gradlew.bat :androidApp:assembleDebug` | Compiles and builds the debug Android APK |
| `.\gradlew.bat :shared:allTests` | Runs all common and platform-specific tests |
| `.\gradlew.bat :shared:androidHostTest` | Runs Android-specific unit tests |

### Running the Apps

#### Android
Open the project in Android Studio, select the `androidApp` run configuration, and press **Run**. Alternatively, install it via command line:
```bash
# Compile and install on a connected device/emulator
./gradlew :androidApp:installDebug
```

#### iOS
1.  Open `iosApp/iosApp.xcodeproj` in Xcode.
2.  Select your target device or simulator.
3.  Click the **Run** button (or press `Cmd + R`).

---

## 🧪 Testing

Shared unit tests are located in `shared/src/commonTest/`. Run them using the IDE gutter icons or invoke:
```bash
# Run all unit tests for common and platform sourcesets
./gradlew :shared:allTests
```

---

## 📝 Coding Guidelines & Conventions

*   **Indentation**: Always use **4 spaces** for Kotlin.
*   **Naming Conventions**:
    *   `PascalCase` for classes, composable functions (e.g. `LoginScreen`), ScreenModels, DTOs, and use cases.
    *   `camelCase` for functions, properties, and local variables.
*   **Commit Messages**: Follow [Conventional Commits](https://www.conventionalcommits.org/):
    *   `feat: implement ...`
    *   `fix: resolve ...`
    *   `refactor: clean up ...`
*   **Secrets Management**: Never commit local paths or API keys to git. Keep configuration keys in `local.properties`. Dependency versions must be defined centrally in `gradle/libs.versions.toml`.