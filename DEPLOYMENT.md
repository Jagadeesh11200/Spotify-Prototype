# Deployment Guide

This project is a native Android Jetpack Compose MVP. Do not convert it into a web app for evaluation. The smoothest
deployment model is:

1. Build a signed/debug APK from this repository.
2. Upload that APK to a hosted Android device streaming service.
3. Share a web URL, optionally wrapped in a tiny evaluator portal hosted on Vercel or Render.

The evaluator opens a URL in their browser and interacts with the real Android app running inside a cloud emulator.

## Recommended Architecture

```mermaid
flowchart LR
  Repo["GitHub repo"] --> Build["Gradle build: ./gradlew assembleDebug"]
  Build --> Apk["app-debug.apk"]
  Apk --> BrowserStack["BrowserStack App Live"]
  BrowserStack --> PublicUrl["Hosted mobile session URL"]
  PublicUrl --> Portal["Optional Vercel or Render evaluator portal"]
  Portal --> Evaluator["Evaluator opens URL in browser"]
```

## Why This Fits The MVP

- The product remains a mobile application.
- The evaluator does not need Android Studio.
- The evaluator does not need to download an APK.
- The evaluator does not need a physical Android phone.
- The URL can be shared like a web deployment, but the experience is still a native Android emulator session.

## Build The APK

From the project root:

```powershell
.\gradlew.bat assembleDebug
```

If your terminal says `JAVA_HOME is not set`, use the local helper script instead:

```powershell
.\build-apk.bat
```

The APK will be generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

For this fellowship MVP, the debug APK is acceptable because the app uses mock/local data and is not being distributed
through the Play Store. If you later need Play Store distribution, create a proper release keystore and run
`assembleRelease`.

## Primary Deployment Option: BrowserStack App Live

Use BrowserStack App Live or a similar hosted Android device provider.

High-level steps:

1. Create a BrowserStack account.
2. Copy your BrowserStack username and access key.
3. Add them as GitHub Actions secrets.
4. Let GitHub Actions upload `app/build/outputs/apk/debug/app-debug.apk`.
5. GitHub Actions generates a BrowserStack App Live launch URL.
6. Vercel deploys the evaluator portal.
7. Send the Vercel URL to the evaluator.

Recommended session settings:

- Network: no special network required.
- Launch package: `com.aistudio.spotify.gmpqlr`.
- Main activity: `com.example.MainActivity`.

## Optional Vercel/Render Evaluator Portal

If the fellowship expects a polished single URL, host the static folder in:

```text
deploy/evaluator-portal
```

The portal is not the app. It is only a wrapper page that embeds or links to the hosted Android emulator session.

For manual deployment, edit:

```text
deploy/evaluator-portal/app-config.js
```

and set the BrowserStack App Live URL. For CI/CD, do not edit the generated config on every build; let GitHub Actions
create it.

## Continuous Deployment

This repository includes:

```text
.github/workflows/deploy-mvp.yml
deploy/evaluator-portal/app-config.js
```

Recommended automated flow:

```mermaid
flowchart LR
  Push["Push to main"] --> Actions["GitHub Actions"]
  Actions --> Tests["Run unit tests"]
  Tests --> Apk["Build app-debug.apk"]
  Apk --> BrowserStack["Upload APK to BrowserStack"]
  BrowserStack --> Config["Generate app-config.js"]
  Config --> Vercel["Deploy portal to Vercel"]
  Vercel --> Evaluator["Evaluator opens stable Vercel URL"]
```

One-time setup:

1. Create the GitHub repository and push this code.
2. Create a BrowserStack account.
3. Copy your BrowserStack username and access key.
4. Create a Vercel project for `deploy/evaluator-portal`.
5. Add the required GitHub repository secrets.
6. Push to the `main` branch or run the workflow manually from GitHub Actions.

Required GitHub secrets:

```text
BROWSERSTACK_USERNAME
BROWSERSTACK_ACCESS_KEY
VERCEL_TOKEN
VERCEL_ORG_ID
VERCEL_PROJECT_ID
```

Optional GitHub secret:

```text
BROWSERSTACK_APP_LIVE_URL
BROWSERSTACK_SPEED
```

Use `BROWSERSTACK_APP_LIVE_URL` only if you want to provide a manually prepared BrowserStack launch URL instead of the
one generated from the uploaded APK.

The current evaluator portal opens BrowserStack in a new tab. It does not embed a device iframe.

The workflow keeps the BrowserStack launch generic and does not pin a device, OS, orientation, or display scale:

```text
Speed: 1
```

Override the speed only if needed with:

```text
BROWSERSTACK_SPEED
```

Important: the workflow uploads the latest APK to BrowserStack on every push and then deploys the Vercel portal with
the latest BrowserStack launch URL.

## Manual Vercel Static Deployment

1. Push this repository to GitHub.
2. Open Vercel.
3. Import the repository.
4. Set the project root/directory to:

```text
deploy/evaluator-portal
```

5. Use static site defaults. No build command is required.
6. Deploy.
7. Open the Vercel URL and confirm the emulator loads.

## Manual Render Static Deployment

1. Push this repository to GitHub.
2. Open Render.
3. Create a new Static Site.
4. Select this repository.
5. Set the publish directory to:

```text
deploy/evaluator-portal
```

6. Leave build command blank, or use:

```text
echo "static"
```

7. Deploy.
8. Open the Render URL and confirm the emulator loads.

## Fallback Options

### Google Play Internal Testing

Good for realistic mobile-device testing, but the evaluator must install the app on an Android device. This does not
match the "no APK download" requirement as well as hosted emulator streaming.

### Firebase App Distribution

Good for tester management, but the evaluator still installs the APK on a phone. Use this only if the evaluator agrees
to install an app.

### Appetize

Good for public emulator-style demos, but quota/rate limits may affect the evaluator experience. Keep it as a backup
only if BrowserStack is unavailable.

## Pre-Evaluation Checklist

- Run `.\gradlew.bat testDebugUnitTest`.
- Run `.\gradlew.bat assembleDebug`.
- Upload the latest `app-debug.apk`.
- Open the hosted emulator link in a private browser window.
- Confirm the app starts on the Mood Board screen.
- Confirm "Go to home" works.
- Confirm selecting a mood shows Home/Music with "Right Now For You".
- Confirm bottom navigation works.
- Confirm mini player/full player can open.
- Confirm the evaluator URL does not require login.

## Current App Identity

```text
Application ID: com.aistudio.spotify.gmpqlr
Main Activity:  com.example.MainActivity
Minimum SDK:    24
Target SDK:     36
```
