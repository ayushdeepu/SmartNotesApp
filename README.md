# Smart Notes App 📝

A clean, offline-first **note-taking app for Android**, built as a college
mini-project by **Ayush Kumar**.

> Write it once — it saves itself. Pinned notes stay on top, colours work as
> labels, and everything is stored privately on the device.

---

## ✨ Features

| Feature | Description |
|---|---|
| Create / edit notes | Full editor with title + body text |
| Auto-save | Every change is saved automatically ~0.7 s after typing stops |
| Delete | Confirmation dialog from the editor's top bar |
| Pin notes | Pinned notes always appear at the top of the list |
| Colour labels | 8 pastel colours act as visual labels for notes |
| Live search | Instant, case-insensitive search across titles and content |
| Offline-first | All notes stored locally in a Room (SQLite) database — no internet needed |
| Modern UI | Jetpack Compose + Material 3, edge-to-edge layout, empty-state screen |

## 🛠 Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3 (single-activity app)
- **Architecture:** MVVM (Repository → ViewModel → UI)
- **Database:** Room 2.6 (SQLite) with Kotlin Coroutines + Flow
- **Build:** Gradle 8.4 (Kotlin DSL) + AGP 8.2.2 + Kotlin 1.9.22

## 📁 Project Structure

```
SmartNotesApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/ayush/smartnotes/
│   │   │   ├── MainActivity.kt              # single-activity host, screen switching
│   │   │   ├── data/
│   │   │   │   ├── Note.kt                  # Room entity
│   │   │   │   ├── NoteDao.kt               # queries (list, search, upsert, delete)
│   │   │   │   ├── NotesDatabase.kt         # Room database (singleton)
│   │   │   │   └── NotesRepository.kt       # repository layer (MVVM)
│   │   │   └── ui/
│   │   │       ├── NotesViewModel.kt        # ViewModel with search + list state
│   │   │       ├── TimeFormat.kt            # date formatting helper
│   │   │       ├── screens/
│   │   │       │   ├── NotesListScreen.kt   # home screen (grid, search, FAB)
│   │   │       │   └── NoteEditorScreen.kt  # editor (auto-save, pin, colours, delete)
│   │   │       └── theme/                   # Material 3 theme, colours, typography
│   │   ├── res/                             # strings, themes, adaptive launcher icons
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/wrapper/                          # Official Gradle wrapper jar + properties
├── gradlew / gradlew.bat
├── build.gradle.kts · settings.gradle.kts · gradle.properties
├── .github/workflows/android.yml             # GitHub Actions builds the APK
├── tools/check_project.py                  # Offline project checks
├── GITHUB_UPLOAD_GUIDE.txt                 # Step-by-step upload instructions
├── .gitignore · LICENSE · README.md
```

## 🚀 How to Run

1. **Clone / download** this repository.
2. Open **Android Studio** (Hedgehog or newer) → *Open* → select the
   `SmartNotesApp` folder.
3. Let Gradle **sync** (needs an internet connection the first time).
4. Press **Run ▶** on an emulator or a real device (Android 8.0 / API 26+).

> **Note:** the Gradle wrapper files (`gradlew`, `gradle-wrapper.jar`) are
> included. Open the project folder itself (the directory containing `settings.gradle.kts`).
> Use the JDK 17 runtime in Android Studio. Gradle will download the distribution
> and dependencies during the first sync.

**Requirements:** Android Studio Hedgehog+ · JDK 17 · Android SDK 34

## 🧩 Build a Signed APK

```
Build > Generate Signed App Bundle / APK  (in Android Studio)
```
or from the command line:
```
./gradlew assembleDebug      # debug APK in app/build/outputs/apk/debug/
```

## ☁️ Push to GitHub

**For beginner-friendly browser uploads, follow [GITHUB_UPLOAD_GUIDE.txt](GITHUB_UPLOAD_GUIDE.txt).** Include the `.github` and `tools` directories. You do not need to add a second README.

For command-line Git users:

```bash
git init
git add .
git commit -m "Initial commit: Smart Notes App (Kotlin + Compose + Room)"
git branch -M main
git remote add origin https://github.com/<your-username>/SmartNotesApp.git
git push -u origin main
```

## 📄 License

Released under the [MIT License](LICENSE).

## 👤 Author

**Ayush Kumar** — BCA, Amity University Jharkhand
Project year: 2026

## ✅ Verification and known limits

- The official Gradle wrapper JAR is included in `gradle/wrapper/`.
- Room `@Upsert` safely updates existing notes; saving completes before navigating back.
- System Back and the toolbar Back use the same saving path; deleting asks for confirmation.
- CI validates project structure, then builds the debug APK using JDK 17 and Android SDK 34 after pushing to GitHub.
- The CI script `tools/check_project.py` is included (the previous missing-file issue is fixed).
- **A full APK build and emulator test were not available in the review environment.**
- This project is a **light-theme, two-column grid** design. Any previous dark-themed
  image mockup is a concept, not a screenshot of this Kotlin app.

See [VALIDATION.md](VALIDATION.md) for review notes.
