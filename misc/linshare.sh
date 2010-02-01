#!/bin/bash

export JETTY_OPTS=-Dorg.linshare.demo=true
`dirname $0`/bin/jetty.sh $1 $2 $3 $4 $5 $6 $7 $8 $9
