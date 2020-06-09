package ch.skymarshall.dataflowmgr.examples.simple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlowReport {

	public static class ReportEntry {
		public final String name;
		public final String threadName;

		public ReportEntry(final String name) {
			this.name = name;
			this.threadName = Thread.currentThread().getName();
		}

		@Override
		public String toString() {
			return name + ":[" + threadName + "]";
		}
	}

	public static final List<ReportEntry> report = Collections.synchronizedList(new ArrayList<>());

}
