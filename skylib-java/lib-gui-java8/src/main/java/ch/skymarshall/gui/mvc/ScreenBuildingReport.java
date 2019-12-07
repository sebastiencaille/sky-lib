package ch.skymarshall.gui.mvc;

public class ScreenBuildingReport {

	private static ScreenBuildingReport screenBuildingReport;

	public static void setScreenBuildingReport(final ScreenBuildingReport screenBuildingReport) {
		ScreenBuildingReport.screenBuildingReport = screenBuildingReport;
	}

	private final StringBuilder report = new StringBuilder();

	@Override
	public String toString() {
		return report.toString();
	}

	public static void addDependency(final Object from, final Object to) {
		if (screenBuildingReport != null) {
			screenBuildingReport.report.append(from).append(" -> ").append(to).append('\n');
		}
	}

}
