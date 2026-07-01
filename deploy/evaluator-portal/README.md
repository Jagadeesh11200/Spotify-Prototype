# Evaluator Portal

This folder is a static wrapper for the hosted Android session link. It is not a web version of the app.

The stable portal includes:

```text
index.html
evaluator-guide.html
system-design.html
```

`evaluator-guide.html` gives evaluators a phase-by-phase test script. `system-design.html` explains how the AI-native
review analysis and discovery backend maps to the MVP.

The portal opens BrowserStack App Live in a new tab through the `Open Mobile Session` button.

## Local/manual setup

For a manual deployment, update `app-config.js` with your BrowserStack App Live URL and deploy this folder as a static
site.

## CI/CD setup

The GitHub Actions workflow at `.github/workflows/deploy-mvp.yml` generates `app-config.js` automatically during each
production deployment. In that mode, do not hand-edit `index.html` for every build.

Required GitHub secrets:

```text
BROWSERSTACK_USERNAME
BROWSERSTACK_ACCESS_KEY
VERCEL_TOKEN
VERCEL_ORG_ID
VERCEL_PROJECT_ID
```

Optional GitHub secrets:

```text
BROWSERSTACK_APP_LIVE_URL
BROWSERSTACK_DEVICE
BROWSERSTACK_OS_VERSION
BROWSERSTACK_SPEED
```

Default BrowserStack launch configuration:

```text
Device: OnePlus 11R
OS: Android 13.0
Display: scale_to_fit=true
Speed: 1
Start: true
```

Set the optional BrowserStack secrets only if the selected device/OS is unavailable in your plan or you want to pin a
specific App Live launch URL manually.
