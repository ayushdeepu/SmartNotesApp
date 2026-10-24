#!/usr/bin/env python3
"""Offline sanity checks for the Smart Notes Android Studio project.

This does not replace a complete Gradle build or Android device test.
"""
from __future__ import annotations

import sys
import zipfile
from pathlib import Path
from xml.etree import ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]

REQUIRED = [
    '.github/workflows/android.yml',
    '.gitignore',
    'README.md',
    'settings.gradle.kts',
    'build.gradle.kts',
    'gradle.properties',
    'gradlew',
    'gradlew.bat',
    'gradle/wrapper/gradle-wrapper.jar',
    'gradle/wrapper/gradle-wrapper.properties',
    'app/build.gradle.kts',
    'app/src/main/AndroidManifest.xml',
    'app/src/main/java/com/ayush/smartnotes/MainActivity.kt',
    'app/src/main/java/com/ayush/smartnotes/data/Note.kt',
    'app/src/main/java/com/ayush/smartnotes/data/NoteDao.kt',
    'app/src/main/java/com/ayush/smartnotes/data/NotesDatabase.kt',
    'app/src/main/java/com/ayush/smartnotes/data/NotesRepository.kt',
    'app/src/main/java/com/ayush/smartnotes/ui/NotesViewModel.kt',
    'app/src/main/java/com/ayush/smartnotes/ui/screens/NoteEditorScreen.kt',
    'app/src/main/java/com/ayush/smartnotes/ui/screens/NotesListScreen.kt',
]


def check() -> None:
    errors: list[str] = []
    for relative in REQUIRED:
        if not (ROOT / relative).is_file():
            errors.append('Missing file: ' + relative)

    xml_files = sorted((ROOT / 'app/src/main').rglob('*.xml'))
    if not xml_files:
        errors.append('No Android XML resources found')
    for file in xml_files:
        try:
            ET.parse(file)
        except ET.ParseError as exc:
            errors.append(f'Invalid XML: {file.relative_to(ROOT)} ({exc})')

    wrapper = ROOT / 'gradle/wrapper/gradle-wrapper.jar'
    if wrapper.is_file():
        try:
            with zipfile.ZipFile(wrapper) as archive:
                if archive.testzip() is not None:
                    errors.append('Gradle wrapper jar is corrupt')
                if 'org/gradle/wrapper/GradleWrapperMain.class' not in archive.namelist():
                    errors.append('Gradle wrapper jar lacks GradleWrapperMain')
        except zipfile.BadZipFile:
            errors.append('Invalid Gradle wrapper jar')

    def must_contain(path: str, needle: str) -> None:
        f = ROOT / path
        if f.is_file() and needle not in f.read_text(encoding='utf-8'):
            errors.append(f'{path} does not contain required {needle!r}')

    must_contain('settings.gradle.kts', 'include(":app")')
    must_contain('app/src/main/java/com/ayush/smartnotes/data/NoteDao.kt', '@Upsert')
    must_contain('app/src/main/AndroidManifest.xml', 'android:exported="true"')
    must_contain('app/src/main/java/com/ayush/smartnotes/ui/screens/NoteEditorScreen.kt', 'persistChanges()')
    must_contain('.github/workflows/android.yml', 'assembleDebug')

    if errors:
        print('Project validation FAILED:')
        for message in errors:
            print(' -', message)
        sys.exit(1)

    print(f'Project validation PASSED: {len(REQUIRED)} required files, '
          f'{len(xml_files)} XML resources, Gradle wrapper JAR, and key code references.')
    print('Next step: run ./gradlew assembleDebug for a full Android build.')


if __name__ == '__main__':
    check()
