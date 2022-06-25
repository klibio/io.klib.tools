#!/bin/bash
# activate bash checks for unset vars, pipe fails
set -eauo pipefail
SCRIPT=$(readlink -f "$0")
SCRIPT_DIR=$(dirname "$SCRIPT")

. ${SCRIPT_DIR}/setEnv.sh

echo "# build relevant environment configuration"
env | sort | grep -i '^_.*'

echo -e "\n# launching gradle-wrapper build"
HOME=$PWD/HOME
./gradlew \
  --gradle-user-home=$HOME \
  --info \
  clean \
  tasks \
  release

exit 0
