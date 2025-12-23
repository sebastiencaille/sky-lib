package ch.scaille.tcwriter.model;

public record TestObjectDescription(String description, String humanReadable) {

	public static final TestObjectDescription NOT_SET = new TestObjectDescription("N/A", "N/A");

	public TestObjectDescription() {
		this(null, null);
	}

}
