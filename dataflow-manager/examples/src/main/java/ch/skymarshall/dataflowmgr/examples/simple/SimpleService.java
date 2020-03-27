package ch.skymarshall.dataflowmgr.examples.simple;

import ch.skymarshall.dataflowmgr.annotations.Processor;
import ch.skymarshall.dataflowmgr.examples.simple.dto.MyData;

@Processor
public class SimpleService {

	public MyData init(final String input) {
		return new MyData(input);
	}

	public MyData enhance(final MyData input, final String externalData) {
		return new MyData(input, " -> enhanced with " + externalData);
	}

	public MyData noEnhance(final MyData input) {
		return new MyData(input, " -> not enhanced");
	}

}
