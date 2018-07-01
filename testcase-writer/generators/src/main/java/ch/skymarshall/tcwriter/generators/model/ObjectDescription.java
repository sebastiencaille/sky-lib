package ch.skymarshall.tcwriter.generators.model;

public class ObjectDescription {

	public static final ObjectDescription NOT_SET = new ObjectDescription("N/A", "N/A");
	private final String description;
	private final String stepSummary;

	protected ObjectDescription() {
		description = null;
		stepSummary = null;
	}

	public ObjectDescription(final String description, final String stepSummary) {
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
