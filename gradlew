#!/bin/sh
# Simple gradlew wrapper script
exec java -classpath gradle/wrapper/gradle-wrapper.jar org.gradle.wrapper.GradleWrapperMain "$@"
