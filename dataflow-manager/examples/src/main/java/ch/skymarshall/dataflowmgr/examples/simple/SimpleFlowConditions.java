package ch.skymarshall.dataflowmgr.examples.simple;

import ch.skymarshall.dataflowmgr.annotations.Conditions;
import ch.skymarshall.dataflowmgr.examples.simple.FlowReport.ReportEntry;
import ch.skymarshall.dataflowmgr.examples.simple.dto.MyData;

@Conditions
public class SimpleFlowConditions {

	public boolean mustComplete(final MyData input) {
		FlowReport.add("mustComplete");
		return input.parameter.equals("Hello") || input.parameter.equals("Hi");
	}

}
