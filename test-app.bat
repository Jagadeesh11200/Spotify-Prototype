@echo off
setlocal

set "PROJECT_DIR=%~dp0"
set "JAVA_HOME=%PROJECT_DIR%.tools\jdk\jdk-21.0.11+10"
set "ANDROID_HOME=%PROJECT_DIR%.tools\android-sdk"
set "ANDROID_SDK_ROOT=%ANDROID_HOME%"
set "PATH=%JAVA_HOME%\bin;%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\emulator;%PATH%"

if not exist "%JAVA_HOME%\bin\java.exe" (
  echo ERROR: Local JDK not found at "%JAVA_HOME%".
  exit /b 1
)

call "%PROJECT_DIR%gradlew.bat" testDebugUnitTest
exit /b %ERRORLEVEL%
