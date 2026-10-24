# Smart Notes App — Review Notes

## Issues detected in the uploaded ZIP

1. `gradle-wrapper.jar` was at the repository root rather than inside
   `gradle/wrapper/`, so `gradlew` failed to find `GradleWrapperMain`.
2. Room DAO marked its save method with `@Insert`, which fails for existing
   primary keys. Switched to `@Upsert` for inserts and updates.
3. Editor saved using a coroutine tied to the UI but exited immediately,
   potentially cancelling saves. Saving now finishes before navigation; writes
   are serialized and the system Back gesture uses the same path.

## Additional improvements

- Added a delete-confirmation dialog.
- Preserved editor and screen selection through configuration changes.
- Prevented typing into existing notes before loading completes.
- Fixed the misleading saved-notes count while searching.
- Made `.gitignore` ignore nested build directories.
- Added GitHub Actions workflow that builds the debug APK with JDK 17.

## GitHub Actions workflow repair

- Included `tools/check_project.py`; no workflow step references a missing file.
- CI performs structural checks before `assembleDebug`.
- CI uploads the debug APK as an artifact if the build succeeds.
- The included `GITHUB_UPLOAD_GUIDE.txt` explains how to include dot-folders.

## Review limits

Verified the ZIP's file integrity, resource XML validity, Gradle wrapper
structure, and static source patterns. This environment lacks the Android SDK
and a Gradle distribution, so the APK **was not compiled or run here**.
After uploading, check your repository's Actions tab for an actual successful
Android build. The app also needs a device/emulator smoke test to verify all
interaction paths, including process termination during typing.
