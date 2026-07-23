import org.jspecify.annotations.NullMarked;

@NullMarked
module lib.gui.generators {

	exports ch.scaille.gui.mvc;
	opens templates;

    requires lib.annotations;
    requires lib.utils;
    requires transitive lib.generator.utils;
}