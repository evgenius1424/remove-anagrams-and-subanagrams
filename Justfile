set shell := ["bash", "-eu", "-o", "pipefail", "-c"]

default:
  @just --list

test-all: test-python test-kotlin

test-python:
  @python3 tests/python/run.py

test-kotlin:
  @if command -v /usr/libexec/java_home >/dev/null 2>&1; then \
    export JAVA_HOME="$(/usr/libexec/java_home)"; \
  elif [ -d "/usr/lib/jvm/default-java" ]; then \
    export JAVA_HOME="/usr/lib/jvm/default-java"; \
  elif [ -d "/usr/lib/jvm/java-11-openjdk" ]; then \
    export JAVA_HOME="/usr/lib/jvm/java-11-openjdk"; \
  fi; \
  if [ -z "${JAVA_HOME:-}" ]; then \
    echo "Error: JAVA_HOME not set and could not auto-detect Java installation."; \
    echo "Please install Java or set JAVA_HOME manually."; \
    exit 1; \
  fi; \
  ./gradlew -q run
