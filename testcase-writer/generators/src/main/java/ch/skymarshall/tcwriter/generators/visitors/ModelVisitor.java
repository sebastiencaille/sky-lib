package ch.skymarshall.tcwriter.generators.visitors;

import ch.skymarshall.tcwriter.generators.model.testapi.TestDictionary;

public class ModelVisitor {

	protected final TestDictionary model;

	public ModelVisitor(final TestDictionary model) {
		this.model = model;
	}

}
