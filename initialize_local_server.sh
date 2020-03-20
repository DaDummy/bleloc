#!/bin/bash
BASEDIR="$(cd $(dirname $0) && pwd)"
ASADMIN="$BASEDIR/../glassfish5/bin/asadmin"
[ -e "$BASEDIR/glassfish_settings.sh" ] && source "$BASEDIR/glassfish_settings.sh"

cp "$BASEDIR/guava-27.0.1-jre.jar" "$BASEDIR/../glassfish5/glassfish/modules/guava.jar"

"$ASADMIN" start-domain

"$ASADMIN" create-system-properties deployment.resource.validation=false

"$ASADMIN" stop-domain
