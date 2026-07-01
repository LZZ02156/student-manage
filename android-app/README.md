# Rollcall Android app (MVP)

This is a minimal Android (Kotlin + Jetpack Compose) skeleton that talks to the Rollcall backend.

How to open
1. Open Android Studio and choose "Open" pointing to android-app/ directory.
2. Let Android Studio sync Gradle (you may need to install Android SDK 33, Kotlin plugin, and Gradle).

Configuration
- Backend base URL: the app's Retrofit instance is not yet wired. For quick testing you can set up a local reverse proxy or configure emulator to access host.docker.internal if backend runs in Docker.

Notes
- This is an app skeleton: authentication storage, Retrofit instance, real network calls, navigation, and runtime permissions need to be implemented next.
- I will add concrete Retrofit wiring, token storage (EncryptedSharedPreferences), and sample screens if you want me to continue.
