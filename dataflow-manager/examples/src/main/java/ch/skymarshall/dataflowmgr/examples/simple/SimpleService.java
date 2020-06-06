package ch.skymarshall.dataflowmgr.examples.simple;

import ch.skymarshall.dataflowmgr.annotations.Input;
import ch.skymarshall.dataflowmgr.annotations.Processors;
import ch.skymarshall.dataflowmgr.examples.simple.dto.MyData;

@Processors
public class SimpleService {

	public MyData init(final String input) {
		return new MyData(input);
	}

	public MyData enhance(final MyData input, @Input("enhancement") final String enhancement) {
		return new MyData(input, " -> enhanced with " + enhancement);
	}

	public MyData noEnhance(final MyData input) {
		return new MyData(input, " -> not enhanced");
	}

}
