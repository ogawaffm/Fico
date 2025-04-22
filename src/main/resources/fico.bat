@ECHO OFF
REM Switch to utf-8 for umlauts
chcp 65001>null
SETLOCAL
IF "%JAVA_HOME%" == "" (
    REM https://learn.microsoft.com/de-de/windows-server/administration/windows-commands/where
    REM Use absolute path in case of PATH is not set at all
    %SystemRoot%\system32\where /q java
    if ERRORLEVEL==1 (
        ECHO ERROR: JAVA_HOME is not set and no Java found using PATH variable.
        ECHO.       Set JAVA_HOME to a JDK root directory [Adviced] or extend
        ECHO.       the PATH variable by a reference to a JDK 'bin' directory.
        ECHO.       Version must be at least 11. Check using: java --version
        ECHO.
        REM exit because no Java locatable and signal error
        exit /B 1
    ) ELSE (
        REM see: https://www.robvanderwoude.com/variableexpansion.php
        SETLOCAL ENABLEDELAYEDEXPANSION
        for /f "delims=" %%L in ('where java') do (
            IF "!FIRST_JAVA!" == "" SET FIRST_JAVA=%%L
        )
        ECHO.WARN: JAVA_HOME is not set. Using Java found via PATH variable which is:
        ECHO.      "!FIRST_JAVA!"
        ECHO.      Set JAVA_HOME variable to a JDK root directory [Adviced].
        ECHO.      Version must be at least 11. Check using: java --version
        ECHO.
        SETLOCAL DISABLEDELAYEDEXPANSION
        REM use Java automatically found via PATH variable
        SET FICO_JAVA=java
    )
) ELSE (
    REM use Java from JAVA_HOME
    SET FICO_JAVA=%JAVA_HOME%/bin/java
)
SET APP_DIR=%~dp0
SET JAVA_CP=%~dp0
IF "%1%" == "start" (
  start "Fico Server" "%FICO_JAVA%" %DEBUG_ARGS% -jar %APP_DIR%fico.jar %*
) ELSE (
  IF "%FICO_DEBUG_PORT%" == "" (
    SET DEBUG_ARGS=
  ) ELSE (
    SET DEBUG_ARGS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=127.0.0.1:%FICO_DEBUG_PORT%
  )
  "%FICO_JAVA%" %DEBUG_ARGS% -jar %APP_DIR%fico.jar %*
)
EXIT /B %ERRORLEVEL%