#!/bin/bash

export LINSHARE_HOME=$( cd `dirname $0` && pwd )


export JAVA_OPTIONS="-Dorg.mortbay.jetty.webapp.parentLoaderPriority=true -Dorg.linshare.demo=true -DLINSHARE_HOME=$LINSHARE_HOME -Dlinshare.files.directory=$LINSHARE_HOME/var"

$LINSHARE_HOME/jetty/bin/jetty.sh $1 $2 $3 $4 $5 $6 $7 $8 $9
