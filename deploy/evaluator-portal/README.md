# Evaluator Portal

This folder is a static wrapper for the hosted Android emulator link. It is not a web version of the app.

The stable portal includes:

```text
index.html
evaluator-guide.html
system-design.html
```

`evaluator-guide.html` gives evaluators a phase-by-phase test script. `system-design.html` explains how the AI-native
review analysis and discovery backend maps to the MVP.

The portal intentionally opens Appetize in a new tab instead of embedding it in an iframe. Appetize can refuse iframe
loading on Vercel depending on app/share settings, so the reliable evaluator path is the `Open Mobile Session` button.

## Local/manual setup

For a manual deployment, update `app-config.js` with your Appetize URL and deploy this folder as a static site.

## CI/CD setup

The GitHub Actions workflow at `.github/workflows/deploy-mvp.yml` generates `app-config.js` automatically during each
production deployment. In that mode, do not hand-edit `index.html` for every build.

Required GitHub secrets:

```text
APPETIZE_API_TOKEN
APPETIZE_PUBLIC_KEY
VERCEL_TOKEN
VERCEL_ORG_ID
VERCEL_PROJECT_ID
```

Optional GitHub secret:

```text
APPETIZE_APP_URL
APPETIZE_DEVICE
APPETIZE_OS_VERSION
```

Use `APPETIZE_APP_URL` only if the public Appetize link you want evaluators to open differs from the default:

```text
https://appetize.io/app/{APPETIZE_PUBLIC_KEY}
```

The default evaluator device is:

```text
Device: Pixel 9 Pro
URL value: pixel9pro
OS: Android 16.0
URL value: 16.0
Orientation: portrait
```

Set `APPETIZE_DEVICE` or `APPETIZE_OS_VERSION` only if you want to override this configuration.
