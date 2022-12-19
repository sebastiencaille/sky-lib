#!/bin/sh
export JAVA_HOME=/usr/lib/jvm/java-17-temurin/
for f in skylib-java testcase-writer dataflow-manager; do
	pushd $f
	mvn -Pcoverage install
	mvn -Pcoverage,sonar jacoco:report@report-for-sonar sonar:sonar 
	popd
done


