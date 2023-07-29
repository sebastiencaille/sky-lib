package ch.scaille.tcwriter.services.generators.visitors;

import ch.scaille.tcwriter.model.dictionary.TestDictionary;

public class ModelVisitor {

	protected final TestDictionary model;

	public ModelVisitor(final TestDictionary model) {
		this.model = model;
	}

}
