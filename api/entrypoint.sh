#!/bin/sh

if [ -z $CONFETI_LOCAL_DB ] || [ $CONFETI_LOCAL_DB = true ]; then
    java --enable-preview -jar /confeti.jar
else
    if [ -z $ASTRA_CLIENT_ID ] || [ -z $ASTRA_CLIENT_SECRET ] || [ -z $ASTRA_TOKEN ]; then
        echo "Client id, client secret, token are not set"
        exit 1
    else
        source ./setup.sh -i=$ASTRA_CLIENT_ID -s=$ASTRA_CLIENT_SECRET -t=$ASTRA_TOKEN && java --enable-preview -jar /confeti.jar
    fi
fi
