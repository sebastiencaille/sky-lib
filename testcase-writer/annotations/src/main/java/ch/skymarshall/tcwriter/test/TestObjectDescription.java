package ch.skymarshall.tcwriter.test;

public class TestObjectDescription {

	public static final TestObjectDescription NOT_SET = new TestObjectDescription("N/A", "N/A");
	private final String description;
	private final String stepSummary;

	protected TestObjectDescription() {
		description = null;
		stepSummary = null;
	}

	public TestObjectDescription(final String description, final String stepSummary) {
		this.description = description;
		this.stepSummary = stepSummary;
	}

	public String getDescription() {
		return description;
	}

	public String getStepSummary() {
		return stepSummary;
	}

	@Override
	public String toString() {
		return description + "/" + stepSummary;
	}

}
