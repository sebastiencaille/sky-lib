package ch.scaille.dataflowmgr.examples.simple;

import java.util.Objects;

import ch.scaille.dataflowmgr.annotations.Input;
import ch.scaille.dataflowmgr.annotations.Processors;
import ch.scaille.dataflowmgr.examples.simple.dto.MyData;

@Processors
public class SimpleService {

	public MyData init(final String input) {
		FlowReport.add("init");
		return new MyData(Objects.requireNonNull(input, "input"));
	}

	public MyData complete(final MyData input, @Input("completion") final String completion) {
		FlowReport.add("complete");
		return new MyData(Objects.requireNonNull(input, "input"), 
				" -> complete with " + Objects.requireNonNull(completion, "input"));
	}

	public MyData keepAsIs(final MyData input) {
		FlowReport.add("keepAsIs");
		return new MyData(Objects.requireNonNull(input, "input"), " -> keep as is");
	}

}
