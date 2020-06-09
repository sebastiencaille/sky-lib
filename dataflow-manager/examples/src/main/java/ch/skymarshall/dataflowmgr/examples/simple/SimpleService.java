package ch.skymarshall.dataflowmgr.examples.simple;

import ch.skymarshall.dataflowmgr.annotations.Input;
import ch.skymarshall.dataflowmgr.annotations.Processors;
import ch.skymarshall.dataflowmgr.examples.simple.FlowReport.ReportEntry;
import ch.skymarshall.dataflowmgr.examples.simple.dto.MyData;

@Processors
public class SimpleService {

	public MyData init(final String input) {
		FlowReport.report.add(new ReportEntry("init"));
		return new MyData(input);
	}

	public MyData enhance(final MyData input, @Input("enhancement") final String enhancement) {
		FlowReport.report.add(new ReportEntry("enhance"));
		return new MyData(input, " -> enhanced with " + enhancement);
	}

	public MyData noEnhance(final MyData input) {
		FlowReport.report.add(new ReportEntry("noEnhance"));
		return new MyData(input, " -> not enhanced");
	}

}
