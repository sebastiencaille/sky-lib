package ch.scaille.tcwriter.generators.visitors;

import ch.scaille.tcwriter.model.testapi.TestDictionary;

public class ModelVisitor {

	protected final TestDictionary model;

	public ModelVisitor(final TestDictionary model) {
		this.model = model;
	}

}
