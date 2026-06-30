@echo off
setlocal

set "PROJECT_DIR=%~dp0"
set "JAVA_HOME=%PROJECT_DIR%.tools\jdk\jdk-21.0.11+10"
set "ANDROID_HOME=%PROJECT_DIR%.tools\android-sdk"
set "ANDROID_SDK_ROOT=%ANDROID_HOME%"
set "PATH=%JAVA_HOME%\bin;%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\emulator;%PATH%"

if not exist "%JAVA_HOME%\bin\java.exe" (
  echo ERROR: Local JDK not found at "%JAVA_HOME%".
  echo Run the setup from this Codex thread again or install JDK 21 and set JAVA_HOME manually.
  exit /b 1
)

if not exist "%ANDROID_HOME%\platforms" (
  echo ERROR: Local Android SDK not found at "%ANDROID_HOME%".
  echo Run the setup from this Codex thread again or install Android SDK and set ANDROID_HOME manually.
  exit /b 1
)

call "%PROJECT_DIR%gradlew.bat" assembleDebug
set "GRADLE_EXIT=%ERRORLEVEL%"

if "%GRADLE_EXIT%"=="0" (
  echo.
  echo APK built successfully:
  echo %PROJECT_DIR%app\build\outputs\apk\debug\app-debug.apk
)

exit /b %GRADLE_EXIT%
