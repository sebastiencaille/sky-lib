package ch.scaille.example.gui.model.impl;

import static ch.scaille.gui.model.views.ListViews.sorted;

import ch.scaille.example.gui.TestObject;
import ch.scaille.gui.model.views.IListView;
import ch.scaille.javabeans.converters.IConverter;

public interface Converters {

	static IConverter<Boolean, IListView<TestObject>> booleanToOrder() {
		return ch.scaille.javabeans.converters.Converters.either(
				() -> sorted(TableModelExampleView.NATURAL_ORDER), () -> sorted(TableModelExampleView.REVERSE_ORDER));
	}

}
