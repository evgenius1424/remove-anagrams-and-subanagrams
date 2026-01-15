set shell := ["bash", "-eu", "-o", "pipefail", "-c"]

default:
  @just --list

test-all: test-python test-kotlin

test-python:
  @python3 tests/python/run.py

test-kotlin:
  @if [ -z "${JAVA_HOME:-}" ] && command -v /usr/libexec/java_home >/dev/null 2>&1; then \
    export JAVA_HOME="$(/usr/libexec/java_home)"; \
  fi; \
  ./gradlew -q run
