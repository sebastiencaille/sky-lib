import org.jspecify.annotations.NullMarked;

@NullMarked
module lib.gui.examples {
 
	exports ch.scaille.example.gui.tools to org.hibernate.validator;
	opens ch.scaille.example.gui.tools to lib.utils;
	opens ch.scaille.example.util.dao.metadata to lib.utils;
	
	requires java.desktop;
    requires lib.utils;
    requires lib.annotations;
    requires lib.javabeans;
    requires lib.gui;
    requires lib.gui.validation;
    requires jakarta.annotation;

 
}