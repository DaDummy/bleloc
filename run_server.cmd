@setlocal
@echo off
set "BASEDIR=%~dp0"
set "ASADMIN=%BASEDIR%\..\glassfish5\bin\asadmin.bat"
if exist "%BASEDIR%\glassfish_settings.cmd" call "%BASEDIR%\glassfish_settings.cmd"
call "%ASADMIN%" start-database --dbhost localhost
call "%ASADMIN%" start-domain
start http://localhost:4848/
pause