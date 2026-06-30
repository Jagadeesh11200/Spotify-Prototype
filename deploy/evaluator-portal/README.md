# Evaluator Portal

This folder is a static wrapper for the hosted Android emulator link. It is not a web version of the app.

The stable portal includes:

```text
index.html
user-flow.html
ai-architecture.html
```

`user-flow.html` gives evaluators a phase-by-phase test script. `ai-architecture.html` explains how the AI-native
review analysis and discovery backend maps to the MVP.

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
APPETIZE_EMBED_URL
APPETIZE_DEVICE
APPETIZE_OS_VERSION
```

Use `APPETIZE_APP_URL` only if the public Appetize link you want evaluators to open differs from the default:

```text
https://appetize.io/app/{APPETIZE_PUBLIC_KEY}
```

Use `APPETIZE_EMBED_URL` only if Appetize gives you a specific iframe URL. The embedded preview must use the Appetize
embed route:

```text
https://appetize.io/embed/{BuildId-or-PublicKey}
```

Do not put the normal `https://appetize.io/app/...` share link inside the iframe; Appetize may refuse to render that
page inside Vercel.

The default evaluator device is:

```text
Device: Pixel 9 Pro
URL value: pixel9pro
OS: Android 16.0
URL value: 16.0
Orientation: portrait
```

Set `APPETIZE_DEVICE` or `APPETIZE_OS_VERSION` only if you want to override this configuration.
