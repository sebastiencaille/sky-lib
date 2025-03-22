#!/bin/sh
BUILD="mvn -Pcoverage clean install"
SONAR="mvn -Pcoverage jacoco:merge@merge-for-sonar jacoco:report@report-for-sonar sonar:sonar -Dsonar.token=$SONAR_TOKEN -Dsonar.organization=sebastiencaille-github"

pushd skylib-java
$BUILD
$SONAR -Dsonar.projectKey=sebastiencaille-github_libs
popd

pushd testcase-writer
$BUILD
$SONAR -Dsonar.projectKey=sebastiencaille-github_testcase-writer
popd


