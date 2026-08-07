import org.jspecify.annotations.NullMarked;

@NullMarked
module lib.testing.bdd.runtime {
    exports ch.scaille.testing.bdd.definition;

    requires java.logging;

    requires org.jspecify;

    requires static lombok;

}