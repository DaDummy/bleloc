#!/bin/bash
BASEDIR="$(cd $(dirname $0) && pwd)"
ASADMIN="$BASEDIR/../glassfish5/bin/asadmin"
[ -e "$BASEDIR/glassfish_settings.sh" ] && source "$BASEDIR/glassfish_settings.sh"
"$ASADMIN" deploy --name "bleloc-backend" --contextroot "/backend" --force "$BASEDIR/backend/build/libs/backend-1.0.0-SNAPSHOT.war"
#xdg-open "http://localhost:8080/backoffice/backend/user/login"
