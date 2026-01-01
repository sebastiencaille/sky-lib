package ch.scaille.gui.mvc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ch.scaille.generators.util.Template;
import ch.scaille.util.helpers.ClassFinder.URLClassFinder;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class GeneratorContext {
	private final URLClassFinder classFinder;
	final Map<String, String> properties = new HashMap<>();
	final Set<String> imports = new HashSet<>();

	public GeneratorContext(URLClassFinder classFinder) {
		this.classFinder = classFinder;
	}

	public void addImport(final Class<?> class1) {
		imports.add(class1.getName());
	}

	public void append(final String key, final String value) {
		Template.append(properties, key, value);
	}

	public void appendToList(final String key, final String value) {
		Template.appendToList(properties, key, value);
	}

	public void addImport(final String className) {
		imports.add(classFinder.loadByName(className).getName());
	}

	public void reset() {
		imports.clear();
		properties.clear();
	}

}