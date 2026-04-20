@ECHO OFF
SETLOCAL

set WRAPPER_DIR=%~dp0.mvn\wrapper
set WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar
set WRAPPER_PROPS=%WRAPPER_DIR%\maven-wrapper.properties

if not exist "%WRAPPER_JAR%" (
  echo Downloading Maven Wrapper jar...
  powershell -NoProfile -ExecutionPolicy Bypass -Command ^
    "$ErrorActionPreference='Stop';" ^
    "$props = Get-Content '%WRAPPER_PROPS%';" ^
    "$wrapperUrl = ($props | Where-Object { $_ -like 'wrapperUrl=*' } | ForEach-Object { $_.Substring(11) });" ^
    "if (-not $wrapperUrl) { throw 'wrapperUrl not found in maven-wrapper.properties'; }" ^
    "Invoke-WebRequest -UseBasicParsing -Uri $wrapperUrl -OutFile '%WRAPPER_JAR%'"
  if errorlevel 1 (
    echo Failed to download Maven Wrapper jar.
    exit /b 1
  )
)

if "%JAVA_HOME%"=="" (
  set JAVA_EXE=java
) else (
  set JAVA_EXE=%JAVA_HOME%\bin\java.exe
)

set MAVEN_PROJECTBASEDIR=%~dp0.
"%JAVA_EXE%" -Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR% -classpath "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
set ERROR_CODE=%ERRORLEVEL%

ENDLOCAL & exit /b %ERROR_CODE%
