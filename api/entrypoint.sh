#!/bin/sh

if [ -z $CONFETI_LOCAL_DB ] || [ $CONFETI_LOCAL_DB = true ]; then
    java --enable-preview -jar /confeti.jar
else
    if [ -z $ASTRA_CREDS ] || [ -z $ASTRA_PASSWD ]; then
        echo "Credentials and password not set"
        exit 1
    else
        source ./setup.sh -c=$ASTRA_CREDS -p=$ASTRA_PASSWD && java --enable-preview -jar /confeti.jar
    fi
fi
