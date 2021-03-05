package ch.skymarshall.dataflowmgr.examples.simple;

import ch.skymarshall.dataflowmgr.annotations.Input;
import ch.skymarshall.dataflowmgr.annotations.Processors;
import ch.skymarshall.dataflowmgr.examples.simple.dto.MyData;

@Processors
public class SimpleService {

	public MyData init(final String input) {
		assert input != null;
		FlowReport.add("init");
		return new MyData(input);
	}

	public MyData complete(final MyData input, @Input("completion") final String completion) {
		assert input != null;
		assert completion != null;
		FlowReport.add("complete");
		return new MyData(input, " -> complete with " + completion);
	}

	public MyData keepAsIs(final MyData input) {
		assert input != null;
		FlowReport.add("keepAsIs");
		return new MyData(input, " -> keep as is");
	}

}
