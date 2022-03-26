package ch.scaille.tcwriter.model;

public class TestObjectDescription {

	public static final TestObjectDescription NOT_SET = new TestObjectDescription("N/A", "N/A");
	private final String description;
	private final String humanReadable;

	protected TestObjectDescription() {
		description = null;
		humanReadable = null;
	}

	public TestObjectDescription(final String description, final String humanReadable) {
		this.description = description;
		this.humanReadable = humanReadable;
	}

	public String getDescription() {
		return description;
	}

	public String getHumanReadable() {
		return humanReadable;
	}

	@Override
	public String toString() {
		return description + "/" + humanReadable;
	}

}
