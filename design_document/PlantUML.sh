#!/bin/bash
cd PlantUML
rm *.tex *.latex 2>/dev/null
java -jar ../plantuml.1.2018.11.jar -tlatex:nopreamble *.puml
for i in *.latex; do grep -v '\definecolor{plantucolor' <"$i" >"${i%.latex}.tex"; done
rm *.latex
