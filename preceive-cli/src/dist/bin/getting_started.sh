#!/usr/bin/env bash
# Input data to be submitted to PreCeive REST API
INPUT=corpus/getting_started.json

# File for the responses received
OUTPUT=results/getting_started_responses.json

# Your PreCeive REST API credentials
USR=...
PWD=...

# The PreCeive REST API end points to call
ENDPOINTS="sentiment.sentence=/v1/sentiment?level=sentence emotion.sentence=/v1/emotion?level=sentence"

echo "$INPUT -> $OUTPUT"

./bin/preceive-cli \
   -user ${USR} \
   -password ${PWD} \
   -endpoints ${ENDPOINTS} \
   -output ${OUTPUT} \
   -batch ${INPUT}


