
g++ -g -o tst src-test/GtkTest.cpp src-lib/*.cpp  src-gtk3/*.cpp -Isrc-lib -Isrc-hmicontroller -Isrc-gtk3 `pkg-config gtkmm-3.0 --cflags --libs`
