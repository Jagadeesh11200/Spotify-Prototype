# Spotify Mood MVP

This repository contains the Spotify discovery MVP for the fellowship submission.

The native Android codebase remains in `app/`. The production evaluator link is a browser-hosted mobile replica in
`deploy/evaluator-portal/`, so evaluators can test the MVP on a normal PC browser without APK downloads, emulator
accounts, BrowserStack, Appetize, or device-session limits.

## Production Evaluation Surface

The Vercel deployment serves:

```text
deploy/evaluator-portal/index.html
deploy/evaluator-portal/prototype.html
deploy/evaluator-portal/prototype.css
deploy/evaluator-portal/prototype.js
deploy/evaluator-portal/evaluator-guide.html
deploy/evaluator-portal/system-design.html
deploy/evaluator-portal/app-config.js
```

Open `index.html` first. It links to:

- `Open Prototype`: the browser session for the mobile-style prototype
- `Evaluator Guide`: the phase-by-phase test script
- `System Design`: the AI-native architecture and product logic

Refreshing the prototype tab starts a new browser session and consumes the next 3-card mood set from the 15-card deck.

## CI/CD

GitHub Actions deploys automatically on pushes to `main` using `.github/workflows/deploy-mvp.yml`.

Required repository secrets:

```text
VERCEL_TOKEN
VERCEL_ORG_ID
VERCEL_PROJECT_ID
```

The workflow validates the static portal, generates `app-config.js`, and deploys `deploy/evaluator-portal` to Vercel.

## Local Testing

From the repository root, serve the portal with any static server. For example:

```bash
python -m http.server 5177 --directory deploy/evaluator-portal
```

Then open:

```text
http://127.0.0.1:5177/index.html
```

## Native Android Project

The Android source is still available for reference and future development.

Prerequisites:

- Android Studio
- JDK 21
- Android SDK

To run the native app, open this repository in Android Studio and run the `app` module on an emulator or physical device.
