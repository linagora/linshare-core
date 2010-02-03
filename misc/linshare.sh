#!/bin/bash

export LINSHARE_HOME=$( cd `dirname $0` && pwd )


export JAVA_OPTIONS="-Dorg.linshare.demo=true -Dlinshare.files.directory=$LINSHARE_HOME/var -Dlinshare.config.path=$LINSHARE_HOME/etc"

$LINSHARE_HOME/bin/jetty.sh $1 $2 $3 $4 $5 $6 $7 $8 $9
