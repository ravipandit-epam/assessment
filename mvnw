#!/bin/sh

set -e

BASE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
WRAPPER_DIR="$BASE_DIR/.mvn/wrapper"
WRAPPER_JAR="$WRAPPER_DIR/maven-wrapper.jar"
WRAPPER_PROPS="$WRAPPER_DIR/maven-wrapper.properties"

if [ ! -f "$WRAPPER_JAR" ]; then
  echo "Downloading Maven Wrapper jar..."
  WRAPPER_URL=$(grep '^wrapperUrl=' "$WRAPPER_PROPS" | sed 's/^wrapperUrl=//')
  if [ -z "$WRAPPER_URL" ]; then
    echo "wrapperUrl not found in $WRAPPER_PROPS"
    exit 1
  fi

  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$WRAPPER_URL" -o "$WRAPPER_JAR"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$WRAPPER_URL" -O "$WRAPPER_JAR"
  else
    echo "Neither curl nor wget is available to download Maven Wrapper jar."
    exit 1
  fi
fi

exec "${JAVA_HOME:+$JAVA_HOME/bin/}java" \
  -classpath "$WRAPPER_JAR" \
  -Dmaven.multiModuleProjectDirectory="$BASE_DIR" \
  org.apache.maven.wrapper.MavenWrapperMain "$@"
