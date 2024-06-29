@ECHO OFF
SETLOCAL
IF "%JAVA_HOME%" == "" (
    REM https://learn.microsoft.com/de-de/windows-server/administration/windows-commands/where
    REM Use absolute path in case of PATH is not set at all
    %SystemRoot%\system32\where /q java
    if ERRORLEVEL==1 (
        echo ERROR: JAVA_HOME is not set and no Java found using PATH variable.
        echo.       Set JAVA_HOME to a JDK root directory [Adviced] or extend
        echo.       the PATH variable by a reference to a JDK 'bin' directory.
        echo.       Version must be at least 11. Check using: java --version
        echo.
        REM exit because no Java locatable and signal error
        exit /B 1
    ) else (
        REM see: https://www.robvanderwoude.com/variableexpansion.php
        SETLOCAL ENABLEDELAYEDEXPANSION
        for /f "delims=" %%L in ('where java') do (
            IF "!FIRST_JAVA!" == "" SET FIRST_JAVA=%%L
        )
        echo.WARN: JAVA_HOME is not set. Using Java found via PATH variable which is:
        echo.      "!FIRST_JAVA!"
        echo.      Set JAVA_HOME variable to a JDK root directory [Adviced].
        echo.      Version must be at least 11. Check using: java --version
        echo.
        SETLOCAL DISABLEDELAYEDEXPANSION
        REM use Java automatically found via PATH variable
        SET JAVA=java
    )
) else (
    REM use Java from JAVA_HOME
    SET JAVA=%JAVA_HOME%/bin/java
)
SET APP_DIR=%~dp0
SET SAP_LIB=%APP_DIR%sap\jco
SET JAVA_CP=%~dp0
"%JAVA%" -jar fico.jar %*
exit /B %errorlevel%