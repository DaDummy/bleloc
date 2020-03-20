@setlocal
@echo off
set "BASEDIR=%~dp0"
set "ASADMIN=%BASEDIR%\..\glassfish5\bin\asadmin.bat"
if exist "%BASEDIR%\glassfish_settings.cmd" call "%BASEDIR%\glassfish_settings.cmd"
call "%ASADMIN%" deploy --name "bleloc-backend" --contextroot "/backend" --force "%BASEDIR%\backend\build\libs\backend-1.0.0-SNAPSHOT.war"
pause