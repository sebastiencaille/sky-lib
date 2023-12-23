package ch.scaille.tcwriter.model.testcase;

import ch.scaille.tcwriter.model.TestObjectDescription;
import ch.scaille.tcwriter.model.dictionary.TestParameterFactory;

public class TestReference extends TestParameterFactory {

	protected TestStep step;
	protected String description;

	public TestReference(final TestStep step, final String name, final String description) {
		super(name, name, ParameterNature.REFERENCE, step.getAction().getReturnType());
		this.step = step;
		this.description = description;
	}

	public TestReference rename(final String newName, final String description) {
		super.setName(newName);
		this.description = description;
		return this;
	}

	public TestStep getStep() {
		return step;
	}

	public TestObjectDescription toDescription() {
		return new TestObjectDescription("[" + getName() + " from step " + getStep().getOrdinal() + "] " + description,
				description);
	}
	
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
