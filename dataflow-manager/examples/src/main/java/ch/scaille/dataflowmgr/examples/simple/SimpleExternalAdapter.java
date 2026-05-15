package ch.scaille.dataflowmgr.examples.simple;

import java.util.Objects;

import ch.scaille.dataflowmgr.annotations.ExternalAdapters;
import ch.scaille.dataflowmgr.examples.simple.dto.MyData;

@ExternalAdapters
public class SimpleExternalAdapter {

	private String output;

	public void reset() {
		output = null;
	}

	public String getCompletion(final MyData input) {
		FlowReport.add("getCompletion");
        return switch (input.parameter()) {
            case "Hello" -> "World";
            case "Hi" -> "There";
            default -> throw new IllegalStateException("Unknown id: " + input);
        };
	}

	public void display(final MyData result) {
		FlowReport.add("display");
		this.output = Objects.requireNonNull(result, "Result must not be null").output();
	}

	public String getOutput() {
		return output;
	}
}
