package ch.skymarshall.tcwriter.generators.model;

public class TestReference extends TestParameter {

	private final TestStep step;
	private String description;

	public TestReference(final TestStep step, final String name, final String type, final String description) {
		super(name, name, ParameterNature.REFERENCE, type);
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

	public ObjectDescription toDescription() {
		return new ObjectDescription(getName() + ": (step " + step.getOrdinal() + ") ", description);
	}
}
