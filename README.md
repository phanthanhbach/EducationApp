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

## 📸 Quick Preview

| Phone (Student) | Phone (Teacher) | Tablet (Overview) |
| :---: | :---: | :---: |
| <img src="docs/screenshots/phone/student/dashboard_student.png" width="220" alt="Student Dashboard"/> | <img src="docs/screenshots/phone/teacher/dashboard_teacher.png" width="220" alt="Teacher Dashboard"/> | <img src="docs/screenshots/tablet/sidebar.png" width="320" alt="Tablet Layout"/> |

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

| Task / Command                            | Description                                            |
|-------------------------------------------|--------------------------------------------------------|
| `.\gradlew.bat build`                     | Runs compilation and validation checks for all targets |
| `.\gradlew.bat :androidApp:assembleDebug` | Compiles and builds the debug Android APK              |
| `.\gradlew.bat :shared:allTests`          | Runs all common and platform-specific tests            |
| `.\gradlew.bat :shared:androidHostTest`   | Runs Android-specific unit tests                       |

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

## 🖼️ Full Screenshots Gallery

Below is the complete gallery of application screens categorized by device type and user role. Click each dropdown to expand:

<details>
<summary>📱 <b>Phone Screenshots (Expand to View)</b></summary>
<br/>

### 🔐 System & Auth
| Sign In (Light) | Sign In (Dark) | Settings (Light) | Settings (Dark) |
| :---: | :---: | :---: | :---: |
| <img src="docs/screenshots/phone/sign_in_light.png" width="180" alt="Sign In Light"/> | <img src="docs/screenshots/phone/sign_in_dark.png" width="180" alt="Sign In Dark"/> | <img src="docs/screenshots/phone/settings_light.png" width="180" alt="Settings Light"/> | <img src="docs/screenshots/phone/settings_dark.png" width="180" alt="Settings Dark"/> |

### 👨‍🎓 Student Role
| Dashboard | My Classes | Assignments |
| :---: | :---: | :---: |
| <img src="docs/screenshots/phone/student/dashboard_student.png" width="180" alt="Student Dashboard"/> | <img src="docs/screenshots/phone/student/my_classes_student.png" width="180" alt="My Classes"/> | <img src="docs/screenshots/phone/student/assignment_student.png" width="180" alt="Assignments"/> |

| Schedule | Feedback | Payments |
| :---: | :---: | :---: |
| <img src="docs/screenshots/phone/student/schedule_student.png" width="180" alt="Schedule"/> | <img src="docs/screenshots/phone/student/feedback_student.png" width="180" alt="Feedback"/> | <img src="docs/screenshots/phone/student/payments_student.png" width="180" alt="Payments"/> |

| Class Payment | Edit Profile | Profile |
| :---: | :---: | :---: |
| <img src="docs/screenshots/phone/student/class_payment_student.png" width="180" alt="Class Payment"/> | <img src="docs/screenshots/phone/student/edit_profile_student.png" width="180" alt="Edit Profile"/> | <img src="docs/screenshots/phone/student/profile_student.png" width="180" alt="Profile"/> |

### 👩‍🏫 Teacher Role
| Dashboard | My Classes | Schedule |
| :---: | :---: | :---: |
| <img src="docs/screenshots/phone/teacher/dashboard_teacher.png" width="180" alt="Teacher Dashboard"/> | <img src="docs/screenshots/phone/teacher/my_classes_teacher.png" width="180" alt="My Classes"/> | <img src="docs/screenshots/phone/teacher/schedule_teacher.png" width="180" alt="Schedule"/> |

| Attendance | Session Detail | Feedback |
| :---: | :---: | :---: |
| <img src="docs/screenshots/phone/teacher/class_attendance_teacher.png" width="180" alt="Class Attendance"/> | <img src="docs/screenshots/phone/teacher/session_detail_teacher.png" width="180" alt="Session Detail"/> | <img src="docs/screenshots/phone/teacher/feedback_teacher.png" width="180" alt="Feedback"/> |

| Assignments | Assignment Detail | Edit Profile | Profile |
| :---: | :---: | :---: | :---: |
| <img src="docs/screenshots/phone/teacher/assignment_teacher.png" width="180" alt="Assignments"/> | <img src="docs/screenshots/phone/teacher/assignment_detail_teacher.png" width="180" alt="Assignment Detail"/> | <img src="docs/screenshots/phone/teacher/edit_profile_teacher.png" width="180" alt="Edit Profile"/> | <img src="docs/screenshots/phone/teacher/profile_teacher.png" width="180" alt="Profile"/> |

### 👪 Parent Role
| My Children | Child Schedule | Attendance Rate | Feedback |
| :---: | :---: | :---: | :---: |
| <img src="docs/screenshots/phone/parent/my_childern_parent.png" width="180" alt="My Children"/> | <img src="docs/screenshots/phone/parent/child_schedule_parent.png" width="180" alt="Child Schedule"/> | <img src="docs/screenshots/phone/parent/child_attendance_rate_parent.png" width="180" alt="Attendance Rate"/> | <img src="docs/screenshots/phone/parent/feedback_parent.png" width="180" alt="Feedback"/> |

| Feedback Detail | Payments | Profile |
| :---: | :---: | :---: |
| <img src="docs/screenshots/phone/parent/feedback_detail_parent.png" width="180" alt="Feedback Detail"/> | <img src="docs/screenshots/phone/parent/payments_parent.png" width="180" alt="Payments"/> | <img src="docs/screenshots/phone/parent/profile_parent.png" width="180" alt="Profile"/> |

</details>

<details>
<summary>💻 <b>Tablet Screenshots (Expand to View)</b></summary>
<br/>

### 🔐 System & Layout
| Sidebar Navigation | Sign In (Light) | Sign In (Dark) | Settings (Light) | Settings (Dark) |
| :---: | :---: | :---: | :---: | :---: |
| <img src="docs/screenshots/tablet/sidebar.png" width="220" alt="Sidebar Navigation"/> | <img src="docs/screenshots/tablet/sign_in_light.png" width="220" alt="Sign In Light"/> | <img src="docs/screenshots/tablet/sign_in_dark.png" width="220" alt="Sign In Dark"/> | <img src="docs/screenshots/tablet/settings_light.png" width="220" alt="Settings Light"/> | <img src="docs/screenshots/tablet/settings_dark.png" width="220" alt="Settings Dark"/> |

### 👨‍🎓 Student Role
| Dashboard | My Classes | Schedule |
| :---: | :---: | :---: |
| <img src="docs/screenshots/tablet/student/dashboard_student.png" width="240" alt="Dashboard"/> | <img src="docs/screenshots/tablet/student/my_classes_student.png" width="240" alt="My Classes"/> | <img src="docs/screenshots/tablet/student/schedule_student.png" width="240" alt="Schedule"/> |

| Submissions | Feedback | Payments |
| :---: | :---: | :---: |
| <img src="docs/screenshots/tablet/student/submission_student.png" width="240" alt="Submissions"/> | <img src="docs/screenshots/tablet/student/feedback_student.png" width="240" alt="Feedback"/> | <img src="docs/screenshots/tablet/student/payments_student.png" width="240" alt="Payments"/> |

| Invoices | Dashboard Alt View | Profile |
| :---: | :---: | :---: |
| <img src="docs/screenshots/tablet/student/payment_invoices_student.png" width="240" alt="Invoices"/> | <img src="docs/screenshots/tablet/student/dashboard_2_student.png" width="240" alt="Dashboard Alt View"/> | <img src="docs/screenshots/tablet/student/profile_student.png" width="240" alt="Profile"/> |

### 👩‍🏫 Teacher Role
| Dashboard | My Classes | Schedule |
| :---: | :---: | :---: |
| <img src="docs/screenshots/tablet/teacher/dashboard_teacher.png" width="240" alt="Dashboard"/> | <img src="docs/screenshots/tablet/teacher/my_classes_teacher.png" width="240" alt="My Classes"/> | <img src="docs/screenshots/tablet/teacher/schedule_teacher.png" width="240" alt="Schedule"/> |

| Class Attendance | Session Detail | Assignments |
| :---: | :---: | :---: |
| <img src="docs/screenshots/tablet/teacher/class_attendance_teacher.png" width="240" alt="Attendance"/> | <img src="docs/screenshots/tablet/teacher/session_detai_teacher.png" width="240" alt="Session Detail"/> | <img src="docs/screenshots/tablet/teacher/assignment_teacher.png" width="240" alt="Assignments"/> |

| Assignment Detail | Profile |
| :---: | :---: |
| <img src="docs/screenshots/tablet/teacher/assignment_detail_teacher.png" width="240" alt="Assignment Detail"/> | <img src="docs/screenshots/tablet/teacher/profile_teacher.png" width="240" alt="Profile"/> |

### 👪 Parent Role
| My Children | Child Schedule | Attendance Rate |
| :---: | :---: | :---: |
| <img src="docs/screenshots/tablet/parent/my_children_parent.png" width="240" alt="My Children"/> | <img src="docs/screenshots/tablet/parent/child_schedule_parent.png" width="240" alt="Child Schedule"/> | <img src="docs/screenshots/tablet/parent/attendance_rate_parent.png" width="240" alt="Attendance Rate"/> |

| Feedback | Payments | Profile |
| :---: | :---: | :---: |
| <img src="docs/screenshots/tablet/parent/feedback_parent.png" width="240" alt="Feedback"/> | <img src="docs/screenshots/tablet/parent/payments_parent.png" width="240" alt="Payments"/> | <img src="docs/screenshots/tablet/parent/profile_parent.png" width="240" alt="Profile"/> |

</details>

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