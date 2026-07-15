#!/bin/sh

find . -name "*.java" |xargs sed -i 's/package/paackage/'
sleep 10
find . -name "*.java" |xargs sed -i 's/paackage/package/'

