#!/bin/sh

mvn versions:dependency-updates-aggregate-report versions:plugin-updates-aggregate-report

firefox target/reports/dependency-updates-aggregate-report.html target/reports/plugin-updates-aggregate-report.html
