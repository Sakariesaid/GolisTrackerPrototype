# Golis Tracker — SMS-Based Prototype

A read-only Android prototype that watches Golis/Sahal SMS messages and attempts to extract:

- received amount
- balance
- reference/receipt number
- transaction time

## Important

This is a prototype. The exact Golis SMS format may differ, so `SmsParser.kt` is intentionally easy to edit after you test with a real Golis/Sahal message.

The app does **not**:
- send money
- deposit money
- withdraw money
- request or store a Sahal PIN
- connect to a Golis API

It only reads SMS locally after Android grants the requested permissions.

## Build on GitHub — easiest method

1. Create a new GitHub repository.
2. Upload the contents of this project.
3. Push to `main`.
4. Open **Actions**.
5. Select **Build Golis Tracker APK**.
6. Press **Run workflow**.
7. When it finishes, open the run and download the **GolisTracker-debug** artifact.
8. Extract it and install `app-debug.apk` on your Android phone.

The included GitHub workflow installs Gradle 9.6 and Android SDK 36 automatically.

## Build on your Android phone

Use an Android IDE that can open Gradle projects, such as AndroidIDE.

Open the project folder and let Gradle sync. Then build the debug APK with:

    gradle assembleDebug

The APK will be:

    app/build/outputs/apk/debug/app-debug.apk

## First test

1. Install the APK.
2. Open Golis Tracker.
3. Tap **Allow SMS Access**.
4. Receive a small test transaction on the Golis/Sahal account.
5. Check whether the transaction appears.

The parser currently recognizes common English words such as `Golis`, `Sahal`, `received`, `credited`, and `balance`. If the real SMS wording is different, edit the regular expressions in `SmsParser.kt`.

Do not put your Sahal PIN or other secret credentials into this project.

## Important Android/Google Play note

SMS permissions are sensitive. A sideloaded prototype is different from a public Google Play release. Before publishing, review Google's current SMS/Call Log permission policy and make sure the app qualifies.

## Project structure

- `app/src/main/java/.../MainActivity.kt` — dashboard and permissions
- `SmsReceiver.kt` — receives new SMS events
- `SmsParser.kt` — extracts transaction information
- `AndroidManifest.xml` — SMS permissions
- `.github/workflows/build-apk.yml` — automatic GitHub APK build
