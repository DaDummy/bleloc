cd img/uml_diagrams
set CLASSPATH=..\..
del *.2.svg
for /f "usebackq delims=|" %%f in (`dir /b *.svg`) do java Strip %%f %%f
cd ..\..
pause
