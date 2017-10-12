#!/bin/bash

set -ex

if [ -z $MOVIE_FUN_URL ]; then
  echo "MOVIE_FUN_URL not set"
  exit 1
fi

pushd movie-fun-source
  echo "Running smoke tests for Attendee Service deployed at $MOVIE_FUN_URL"
  mvn test
popd

exit 0