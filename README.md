# SHADOW

Starter Android project for a neon-green/black PIN-screen prototype.

## Build on GitHub from your phone
1. Create a repository named `SHADOW`.
2. Upload the contents of this ZIP to the repository (not the ZIP itself).
3. Open **Actions** and run **Build SHADOW APK**, or push to `main`.
4. Open the completed workflow run and download the `SHADOW-debug-apk` artifact.

## Important limitations
- This is a UI prototype. PIN verification is intentionally not implemented.
- A PIN cannot be verified without retaining some verifier or secret-derived data. Storing absolutely nothing makes verification impossible.
- An ordinary Android app cannot guarantee an unremovable full-device lock. Strong kiosk/device-owner restrictions require supported Android enterprise provisioning and a safe administrator recovery path.
- Do not use this prototype to lock a device you cannot recover.
