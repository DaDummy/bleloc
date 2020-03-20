#!/bin/bash
BASEDIR="$(cd $(dirname $0) && pwd)"
ASADMIN="$BASEDIR/../glassfish5/bin/asadmin"
[ -e "$BASEDIR/glassfish_settings.sh" ] && source "$BASEDIR/glassfish_settings.sh"
"$ASADMIN" deploy --name "bleloc-backoffice" --contextroot "/backoffice" --force "$BASEDIR/backoffice/build/libs/backoffice-1.0.0-SNAPSHOT.war"
xdg-open "http://localhost:8080/backoffice/backoffice/"
