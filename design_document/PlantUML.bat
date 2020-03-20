cd PlantUML
del *.tex *.latex 2>/dev/null
java -jar ../plantuml.1.2018.11.jar -tlatex:nopreamble *.puml
for /f "usebackq delims=|" %%f in (`dir /b *.latex`) do find /V "\definecolor{plantucolor" <%%f >%%~nf.tex
del *.latex
pause
