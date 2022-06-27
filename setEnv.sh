#!/bin/bash
# activate bash checks for unset vars, pipe fails
set -eauo pipefail
# locate script folder and set working directory
SCRIPT=$(readlink -f "$0")
SCRIPT_DIR=$(dirname "$SCRIPT")

echo "# existing environment"
env | sort

FILE_ENV=${SCRIPT_DIR}/env.prop

# local environment configuration
_IMAGE=osgi-demo
_BRANCH=$(git rev-parse --abbrev-ref HEAD)
_DATE=$(date +'%Y.%m.%d-%H.%M.%S')
_VCS_REF=$(git rev-list -1 HEAD)
_VCS_REF_SHORT=$(git describe --dirty --always)
_VERSION=$(cat ${SCRIPT_DIR}/version.txt)
_TAG=${_VERSION}.${_DATE}-${_VCS_REF_SHORT}
major=$(echo ${_VERSION} | sed -E "s/(.*)\.(.*)\.(.*)/\1/g")
minor=$(echo ${_VERSION} | sed -E "s/(.*)\.(.*)\.(.*)/\2/g")
bugfix=$(echo ${_VERSION} | sed -E "s/(.*)\.(.*)\.(.*)/\3/g")

cat <<EOT > $FILE_ENV
_IMAGE=${_IMAGE}
_BRANCH=${_BRANCH}
_DATE=${_DATE}
_VCS_REF=${_VCS_REF}
_VCS_REF_SHORT=${_VCS_REF_SHORT}
_VERSION=${_VERSION}
_TAG=${_VERSION}.${_DATE}-${_VCS_REF_SHORT}
major=${major}
minor=${minor}
bugfix=${bugfix}
EOT

. $FILE_ENV