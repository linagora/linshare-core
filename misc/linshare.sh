#!/bin/bash

export LINSHARE_HOME=$( cd `dirname $0` && pwd )


export JAVA_OPTIONS="-Xmx512m -Xms256m -Dfile.encoding=UTF-8 -Dorg.mortbay.jetty.webapp.parentLoaderPriority=true -DLINSHARE_HOME=$LINSHARE_HOME "

$LINSHARE_HOME/jetty/bin/jetty.sh $1 $2 $3 $4 $5 $6 $7 $8 $9
