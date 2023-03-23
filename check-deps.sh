#!/bin/sh

mvn versions:dependency-updates-aggregate-report versions:plugin-updates-aggregate-report

firefox target/site/dependency-updates-aggregate-report.html target/site/plugin-updates-aggregate-report.html
