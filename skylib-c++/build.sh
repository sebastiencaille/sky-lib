g++ -std=c++17 -o gtk-test `find . -name "*.cpp"` -Isrc-lib -Isrc-gtk4 -Isrc-gtk4-pilot `pkg-config gtkmm-4.0 --cflags --libs`
