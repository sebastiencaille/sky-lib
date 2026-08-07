import org.jspecify.annotations.NullMarked;

@NullMarked
module lib.testing.core {
    requires transitive org.jspecify;
    requires spring.boot.loader;
}