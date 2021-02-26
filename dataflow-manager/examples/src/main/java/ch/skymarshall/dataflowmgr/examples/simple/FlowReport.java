package ch.skymarshall.dataflowmgr.examples.simple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlowReport {

	private FlowReport() {

	}

	public static class ReportEntry {
		public final String threadName;
		public final String text;

		public ReportEntry(final String text) {
			this.text = text;
			this.threadName = Thread.currentThread().getName();
		}

		@Override
		public String toString() {
			return "[" + threadName + "]" + text;
		}
	}

	public static final List<ReportEntry> report = Collections.synchronizedList(new ArrayList<>());
	public static void add(String text) {
		report.add(new ReportEntry(text));
	}

}
