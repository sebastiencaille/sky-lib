package ch.skymarshall.dataflowmgr.examples.simple;

import ch.skymarshall.dataflowmgr.annotations.Conditions;
import ch.skymarshall.dataflowmgr.examples.simple.dto.MyData;

@Conditions
public class SimpleServiceConditions {

	public boolean isEnhanceEnabled(final MyData input) {
		return input.parameter.equals("Hello") || input.parameter.equals("Hi");
	}

}
