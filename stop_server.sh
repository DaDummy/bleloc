#!/bin/bash
BASEDIR="$(cd $(dirname $0) && pwd)"
ASADMIN="$BASEDIR/../glassfish5/bin/asadmin"
[ -e "$BASEDIR/glassfish_settings.sh" ] && source "$BASEDIR/glassfish_settings.sh"
"$ASADMIN" stop-domain
"$ASADMIN" stop-database --dbhost localhost