import org.jspecify.annotations.NullMarked;

@NullMarked
module lib.utils {
    exports ch.scaille.util.helpers;
    exports ch.scaille.util.dao.metadata;
    exports ch.scaille.util.text;
    
    requires static transitive lombok;
    requires transitive java.logging;
    requires transitive org.jspecify;
    requires lib.annotations;
}