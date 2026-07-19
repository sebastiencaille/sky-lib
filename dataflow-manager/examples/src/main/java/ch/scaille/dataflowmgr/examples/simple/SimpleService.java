package ch.scaille.dataflowmgr.examples.simple;

import java.util.Objects;

import ch.scaille.dataflowmgr.annotations.Input;
import ch.scaille.dataflowmgr.annotations.Processors;
import ch.scaille.dataflowmgr.examples.simple.annotations.ExampleApi;
import ch.scaille.dataflowmgr.examples.simple.annotations.ExampleApis;
import ch.scaille.dataflowmgr.examples.simple.dto.MyData;

@ExampleApi(ExampleApis.SIMPLE_SERVICE)
@Processors
public class SimpleService {

	public static final String INPUT = "input";

	@ExampleApi(ExampleApis.INIT)
	public MyData init(final String input) {
		FlowReport.add("0_init");
		return new MyData(Objects.requireNonNull(input, INPUT));
	}

	@ExampleApi(ExampleApis.MUTATE)
	public MyData mutate(final MyData input, @Input("mutation") final String mutation) {
		FlowReport.add("1.0_complete: " + input + ", " + mutation);
		return new MyData(Objects.requireNonNull(input, INPUT),
				" -> complete with " + Objects.requireNonNull(mutation, INPUT));
	}

	@ExampleApi(ExampleApis.KEEP_AS_IS)
	public MyData keepAsIs(final MyData input) {
		FlowReport.add("1.1_keepAsIs: " + input);
		return new MyData(Objects.requireNonNull(input, INPUT), " -> keep as is");
	}

}
