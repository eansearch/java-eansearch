#!/usr/bin/env sh
##############################################################################
# Minimal gradle wrapper shim - delegates to an installed Gradle
# This is a lightweight fallback to satisfy tools that look for a wrapper.
##############################################################################
DIRNAME="$(cd "$(dirname "$0")" && pwd)"
exec gradle "$@"
