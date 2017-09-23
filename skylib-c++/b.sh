#!/bin/bash	
#src-lib/*.cpp  src-gtk3/*.cpp
g++ -g -o tst src-test/TestCompile.cpp src-lib/property.cpp src-lib/property_manager.cpp -Isrc-lib -Isrc-hmicontroller -Isrc-gtk3 `pkg-config gtkmm-3.0 --cflags --libs`
