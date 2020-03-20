@setlocal
@echo off
set "BASEDIR=%~dp0"
set "ASADMIN=%BASEDIR%\..\glassfish5\bin\asadmin.bat"
if exist "%BASEDIR%\glassfish_settings.cmd" call "%BASEDIR%\glassfish_settings.cmd"

xcopy /Y "$BASEDIR/guava-27.0.1-jre.jar" "$BASEDIR/../glassfish5/glassfish/modules/guava.jar"

call "%ASADMIN%" start-domain

call "%ASADMIN%" create-system-properties deployment.resource.validation=false

call "%ASADMIN%" stop-domain

pause