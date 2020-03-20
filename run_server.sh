#!/bin/bash
BASEDIR="$(cd $(dirname $0) && pwd)"
ASADMIN="$BASEDIR/../glassfish5/bin/asadmin"
[ -e "$BASEDIR/glassfish_settings.sh" ] && source "$BASEDIR/glassfish_settings.sh"
"$ASADMIN" start-database --dbhost localhost
"$ASADMIN" start-domain
xdg-open "http://localhost:4848/"
