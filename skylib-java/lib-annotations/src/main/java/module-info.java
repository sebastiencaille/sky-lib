import org.jspecify.annotations.NullMarked;

@NullMarked
module lib.annotations {
    exports ch.scaille.annotations;
    requires org.jspecify;
}