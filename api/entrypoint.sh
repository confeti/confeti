#!/bin/sh

# Set credentials and an Astra DB password
source setup.sh -c='' -p= && java --enable-preview -jar /confeti.jar
