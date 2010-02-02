#!/bin/bash

export LINSHARE_HOME="`dirname $0`"

export JETTY_OPTS="-Dorg.linshare.demo=true -Dlinshare.files.directory=\"$LINSHARE_HOME/var\" -Dlinshare.files.configuration=\"$LINSHARE_HOME/etc\""

$LINSHARE_HOME/bin/jetty.sh $1 $2 $3 $4 $5 $6 $7 $8 $9
