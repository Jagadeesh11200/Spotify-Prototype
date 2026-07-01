# Deployment Guide

## Recommended Submission Path

Keep the existing Android codebase as the native MVP source of truth. For evaluator access, deploy the mobile-first web
replica in `deploy/evaluator-portal` to Vercel as a browser session.

This avoids APK download, emulator account, BrowserStack, and Appetize friction while still letting the evaluator perform the same end-to-end flow:
Mood Board, swipe/click card cycling, preview-song entry, Right Now For You recommendations, active vibe chip, Tune,
clear/cancel, compact tile after repeated exits, and normal player continuity.

Use this wording in the deck:

```text
The MVP was built as a native Android prototype. To remove evaluator friction from APK downloads and third-party
device sessions, the submitted production link opens a browser-hosted mobile-style replica of the same end-to-end
experience. The native Android source remains available in the project repository.
```

## Files Deployed To Vercel

```text
deploy/evaluator-portal/index.html
deploy/evaluator-portal/prototype.html
deploy/evaluator-portal/prototype.css
deploy/evaluator-portal/prototype.js
deploy/evaluator-portal/evaluator-guide.html
deploy/evaluator-portal/system-design.html
deploy/evaluator-portal/app-config.js
```

## CI/CD

The workflow at `.github/workflows/deploy-mvp.yml`:

1. Validates that the evaluator portal files exist.
2. Runs `node --check` on `prototype.js`.
3. Generates portal config pointing to `./prototype.html`.
4. Deploys `deploy/evaluator-portal` to Vercel.

Required GitHub secrets:

```text
VERCEL_TOKEN
VERCEL_ORG_ID
VERCEL_PROJECT_ID
```

## Manual Vercel Deployment

1. Push the repository to GitHub.
2. Create a Vercel project.
3. Set the Vercel project root/output to `deploy/evaluator-portal` as a static site.
4. Deploy.
5. Share the Vercel URL with the evaluator.

The primary evaluator action is `Open Prototype`, which opens `prototype.html` directly.
