@setlocal
@echo off
set "BASEDIR=%~dp0"
set "ASADMIN=%BASEDIR%\..\glassfish5\bin\asadmin.bat"
if exist "%BASEDIR%\glassfish_settings.cmd" call "%BASEDIR%\glassfish_settings.cmd"
call "%ASADMIN%" deploy --name "bleloc-backoffice" --contextroot "/backoffice" --force "%BASEDIR%\backoffice\build\libs\backoffice-1.0.0-SNAPSHOT.war"
start http://localhost:8080/backoffice/backoffice/menu
pause