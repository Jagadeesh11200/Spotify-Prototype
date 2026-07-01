# Evaluator Portal

This folder is the Vercel-hosted evaluator surface for the Spotify Mood MVP.

The Android APK/codebase remains in the repository. For fellowship evaluation, the portal opens a mobile-first web
replica of the same end-to-end MVP experience so evaluators do not need BrowserStack, Appetize, an APK download, or a
third-party login.

The stable portal includes:

```text
index.html
prototype.html
prototype.css
prototype.js
evaluator-guide.html
system-design.html
```

`prototype.html` is the interactive browser session for the mobile-style prototype. `evaluator-guide.html` gives evaluators a
phase-by-phase test script. `system-design.html` explains how the AI-native review analysis and discovery backend maps
to the MVP.

## CI/CD setup

The GitHub Actions workflow at `.github/workflows/deploy-mvp.yml` validates the static evaluator portal, generates
`app-config.js`, and deploys this folder to Vercel. It does not build an APK for production evaluation.

Required GitHub secrets:

```text
VERCEL_TOKEN
VERCEL_ORG_ID
VERCEL_PROJECT_ID
```
