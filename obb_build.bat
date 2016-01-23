@echo off

if "%1." == "." goto usage_help

set fname=main.%1%.de.phbouillon.android.games.alite.obb

java -jar jobb.jar -d Resources -o %fname% -pn de.phbouillon.android.games.alite -pv %1
set ERRORLEV=%ERRORLEVEL%

if NOT "%ERRORLEV%" == "0" goto build_exit

FOR %%A IN (%fname%) DO set fsize=%%~zA

echo.
echo EXTENSION_FILE_LENGTH = %fsize%l;

goto build_exit

:usage_help

echo.
echo Usage:   build_obb ^<obb_version^>

:build_exit

EXIT /B %ERRORLEV%
