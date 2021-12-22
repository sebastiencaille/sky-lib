package ch.scaille.dataflowmgr.examples.simple;

import ch.scaille.dataflowmgr.annotations.Conditions;
import ch.scaille.dataflowmgr.examples.simple.dto.MyData;

@Conditions
public class SimpleFlowConditions {

	public boolean mustComplete(final MyData input) {
		FlowReport.add("mustComplete");
		return input.parameter.equals("Hello") || input.parameter.equals("Hi");
	}

}
