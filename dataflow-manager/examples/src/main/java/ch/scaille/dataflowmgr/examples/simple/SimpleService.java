package ch.scaille.dataflowmgr.examples.simple;

import ch.scaille.dataflowmgr.annotations.Input;
import ch.scaille.dataflowmgr.annotations.Processors;
import ch.scaille.dataflowmgr.examples.simple.dto.MyData;

@Processors
public class SimpleService {

	public MyData init(final String input) {
		if (input == null) {
			throw new IllegalArgumentException("input must be != null");
		}
		FlowReport.add("init");
		return new MyData(input);
	}

	public MyData complete(final MyData input, @Input("completion") final String completion) {
		if (input == null) {
			throw new IllegalArgumentException("input must be != null");
		}
		if (completion == null) {
			throw new IllegalArgumentException("completion must be != null");
		}
		FlowReport.add("complete");
		return new MyData(input, " -> complete with " + completion);
	}

	public MyData keepAsIs(final MyData input) {
		if (input == null) {
			throw new IllegalArgumentException("input must be != null");
		}
		FlowReport.add("keepAsIs");
		return new MyData(input, " -> keep as is");
	}

}
