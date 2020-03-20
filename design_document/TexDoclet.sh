#!/bin/sh

docletpath="texdoclet/TeXDoclet.jar"
doclet="org.stfm.texdoclet.TeXDoclet"
output1="texdoclet/output/backend-classes.tex"
sourcepath1="../backend/src/main/java"
output2="texdoclet/output/app-classes.tex"
sourcepath2="../app/src/main/java"
sectionlevel=section

mkdir texdoclet/output

javadoc -docletpath $docletpath -doclet $doclet -output $output1 -sourcepath $sourcepath1 -include -noindex -hyperref -nosummaries -subpackages edu
javadoc -docletpath $docletpath -doclet $doclet -output $output2 -sourcepath $sourcepath2 -include -noindex -hyperref -nosummaries -subpackages edu

sed -i 's/edu.kit.informatik.pse.//g;s/java.lang.//g' $output1 $output2
