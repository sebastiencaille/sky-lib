package ch.skymarshall.tcwriter.pilot.selenium;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.openqa.selenium.Alert;

import ch.skymarshall.tcwriter.pilot.ModalDialogDetector;
import ch.skymarshall.tcwriter.pilot.ModalDialogDetector.PollingResult;
import ch.skymarshall.util.helpers.NoExceptionCloseable;

public class AlertDetector {

	private AlertDetector() {

	}

	public static void withAlert(final SeleniumGuiPilot pilot, final Runnable runnable) {

		final ModalDialogDetector detector = new ModalDialogDetector(() -> AlertDetector.listAlerts(pilot, null));
		try (NoExceptionCloseable dialogCloseable = ModalDialogDetector.withModalDialogDetection(detector)) {
			runnable.run();
		}
	}

	public static List<ModalDialogDetector.PollingResult> listAlerts(final SeleniumGuiPilot pilot,
			final Function<AlertPilot, PollingResult> errorChecks) {

		final AlertPilot seleniumAlert = new AlertPilot(pilot);
		final Alert alert = seleniumAlert.loadGuiComponent();
		if (alert == null) {
			return Collections.emptyList();
		}
		PollingResult checked = ModalDialogDetector.notHandled("");
		if (errorChecks != null) {
			checked = errorChecks.apply(seleniumAlert);
		}
		if (!checked.handled) {
			checked = ModalDialogDetector.error(alert.getText(), alert::accept);
		}
		return Collections.singletonList(checked);
	}

}
