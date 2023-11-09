package ch.scaille.example.gui.controller.impl;

import java.util.Collections;
import java.util.List;

import ch.scaille.javabeans.converters.IConverter;
import ch.scaille.util.helpers.JavaExt;

public class DynamicListContentConverter implements IConverter<String, List<String>> {

	@Override
	public List<String> convertPropertyValueToComponentValue(final String propertyValue) {

		if (propertyValue == null) {
			return Collections.emptyList();
		}

		switch (propertyValue) {
		case "A":
			return List.of("A", "B", "C");
		case "B":
			return List.of("B", "C", "D");
		case "C":
			return List.of("C", "D", "E");
		default:
			return Collections.emptyList();
		}
	}

	@Override
	public String convertComponentValueToPropertyValue(final List<String> componentValue) {
		throw JavaExt.notImplemented();
	}

}
