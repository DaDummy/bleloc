@echo off
title Javadoc TexDoclet Generator

mkdir texdoclet\output

where javadoc 2> nul
if errorlevel 1 if not "%JAVA_HOME%"=="" set "PATH=%PATH%;%JAVA_HOME%\bin"

SET docletpath="texdoclet\TeXDoclet.jar"
SET doclet="org.stfm.texdoclet.TeXDoclet"
SET output1="texdoclet\output\backend-classes.tex"
SET sourcepath1="..\backend\src\main\java"
SET output2="texdoclet\output\app-classes.tex"
SET sourcepath2="..\app\src\main\java"
SET sectionlevel=section

javadoc -docletpath %docletpath% -doclet %doclet% -output %output1% -sourcepath %sourcepath1% -include -noindex -hyperref -nosummaries -subpackages edu
javadoc -docletpath %docletpath% -doclet %doclet% -output %output2% -sourcepath %sourcepath2% -include -noindex -hyperref -nosummaries -subpackages edu

pause
