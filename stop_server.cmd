@setlocal
@echo off
set "BASEDIR=%~dp0"
set "ASADMIN=%BASEDIR%\..\glassfish5\bin\asadmin.bat"
if exist "%BASEDIR%\glassfish_settings.cmd" call "%BASEDIR%\glassfish_settings.cmd"
call "%ASADMIN%" stop-domain
call "%ASADMIN%" stop-database --dbhost localhost
pause