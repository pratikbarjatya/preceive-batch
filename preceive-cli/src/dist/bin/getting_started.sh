#!/usr/bin/env bash

BINDIR=$(dirname $(readlink -f $0))
# Input data to be submitted to PreCeive REST API
INPUT=${BINDIR}/../corpus/getting_started.json

# File for the responses received
OUTPUT=results/getting_started_responses.json

# Your PreCeive REST API credentials
USERNAME=...
PASSWORD=...

if [ "${USERNAME}" == "..." ]
   then
     printf "\nPlease edit $0 to provide username and password for PreCeive API\n\n"
     exit 1
fi

# The PreCeive REST API end points to call
# <field in json object>=<api endpoint>?level=sentence

ENDPOINTS="sentiment.sentence=/v1/sentiment?level=sentence emotion.sentence=/v1/emotion?level=sentence"

echo "$INPUT -> $OUTPUT"

${BINDIR}/preceive-cli \
   -user ${USERNAME} \
   -password ${PASSWORD} \
   -endpoints ${ENDPOINTS} \
   -output ${OUTPUT} \
   -batch ${INPUT}


