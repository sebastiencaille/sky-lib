package ch.scaille.tcwriter.it.api;

import java.util.Arrays;

import ch.scaille.tcwriter.annotations.TCApi;

public record TestContent(StepEdition[] steps, String[] humanReadable) {
	
	private static final String ACTOR_TEST_WRITER = "Test writer";

	@Override
	public final boolean equals(Object arg0) {
		return (arg0 instanceof TestContent tc) && Arrays.deepEquals(steps, tc.steps) && Arrays.deepEquals(humanReadable, tc.humanReadable);
	}
	
	@TCApi(description = "Basic test", humanReadable = "Basic test")
	public static TestContent basicTestContent() {
		
		final var edition1 = new StepEdition();
		edition1.setActor(ACTOR_TEST_WRITER);
		edition1.setAction("Select a step");
		edition1.setSelector("Append a step to the test");

		final var edition2 = new StepEdition();
		edition2.setActor(ACTOR_TEST_WRITER);
		edition2.setAction("Verify the Human Readable text");
		edition2.setSelector("Selected step");

		final var edition3 = new StepEdition();
		edition3.setActor(ACTOR_TEST_WRITER);
		edition3.setAction("Select a step");
		edition3.setSelector("Step at index");
		edition3.setParameterValue1("index:1");
		
		return new TestContent(new StepEdition[] { edition1, edition2, edition3 },
				new String[] {
						"As test writer, I add a step to the test case",
						"As test writer, I verify that the human readable text is \"\"",
						"As test writer, I select the step 1"
				});
	
	}
	
}
