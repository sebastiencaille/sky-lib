package ch.scaille.dataflowmgr.examples.simple;

import java.util.Objects;

import ch.scaille.dataflowmgr.annotations.ExternalAdapters;
import ch.scaille.dataflowmgr.examples.simple.annotations.ExampleApi;
import ch.scaille.dataflowmgr.examples.simple.annotations.ExampleApis;
import ch.scaille.dataflowmgr.examples.simple.dto.MyData;

@ExternalAdapters
@ExampleApi(ExampleApis.SIMPLE_EXTERNAL_ADAPTER)
public class SimpleExternalAdapter {

	private String output;

	public void reset() {
		output = null;
	}

	@ExampleApi(ExampleApis.GET_MUTATION)
	public String getMutation(final MyData input) {
		FlowReport.add("2_getMutation");
        return switch (input.parameter()) {
            case "Hello" -> "World";
            case "Hi" -> "There";
            default -> throw new IllegalStateException("Unknown id: " + input);
        };
	}

	@ExampleApi(ExampleApis.DISPLAY)
	public void display(final MyData result) {
		FlowReport.add("display");
		this.output = Objects.requireNonNull(result, "Result must not be null").output();
	}

	public String getOutput() {
		return output;
	}
}
