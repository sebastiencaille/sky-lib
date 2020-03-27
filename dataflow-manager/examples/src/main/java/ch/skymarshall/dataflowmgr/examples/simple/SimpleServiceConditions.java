package ch.skymarshall.dataflowmgr.examples.simple;

import ch.skymarshall.dataflowmgr.annotations.Condition;
import ch.skymarshall.dataflowmgr.examples.simple.dto.MyData;

@Condition
public class SimpleServiceConditions {

	public boolean isEnhanceEnabled(final MyData input) {
		return input.parameter.equals("Hello") || input.parameter.equals("Hi");
	}

}
