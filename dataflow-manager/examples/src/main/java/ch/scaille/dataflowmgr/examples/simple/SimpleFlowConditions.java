package ch.scaille.dataflowmgr.examples.simple;

import ch.scaille.dataflowmgr.annotations.Conditions;
import ch.scaille.dataflowmgr.examples.simple.annotations.ExampleApi;
import ch.scaille.dataflowmgr.examples.simple.annotations.ExampleApis;
import ch.scaille.dataflowmgr.examples.simple.dto.MyData;

@Conditions
@ExampleApi(ExampleApis.SIMPLE_FLOW_CONDITIONS)
public class SimpleFlowConditions {

	@ExampleApi(ExampleApis.MUST_MUTATE)
	public boolean mustMutate(final MyData input) {
		final var eval = input.parameter().equals("Hello") || input.parameter().equals("Hi");
		FlowReport.add("1_mustMutate: " + eval);
		return eval;
	}

}
