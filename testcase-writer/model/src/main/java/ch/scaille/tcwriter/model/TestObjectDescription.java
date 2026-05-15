package ch.scaille.tcwriter.model;

public record TestObjectDescription(String description, String humanReadable) {

	public static final TestObjectDescription NOT_SET = new TestObjectDescription();

	public TestObjectDescription() {
		this("N/A", "N/A");
	}

}
