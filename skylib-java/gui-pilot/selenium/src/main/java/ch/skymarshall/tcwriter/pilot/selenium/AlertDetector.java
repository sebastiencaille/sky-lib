package ch.skymarshall.tcwriter.pilot.selenium;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.openqa.selenium.Alert;

import ch.skymarshall.tcwriter.pilot.ModalDialogDetector;
import ch.skymarshall.tcwriter.pilot.ModalDialogDetector.ErrorCheck;
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

	public static List<ModalDialogDetector.ErrorCheck> listAlerts(final SeleniumGuiPilot pilot,
			final Function<SeleniumAlert, ErrorCheck> errorChecks) {

		final SeleniumAlert seleniumAlert = new SeleniumAlert(pilot);
		final Alert alert = seleniumAlert.loadGuiComponent();
		if (alert == null) {
			return Collections.emptyList();
		}
		ErrorCheck checked = ModalDialogDetector.fallback("");
		if (errorChecks != null) {
			checked = errorChecks.apply(seleniumAlert);
		}
		if (!checked.handled) {
			checked = ModalDialogDetector.error(alert.getText(), alert::accept);
		}
		return Collections.singletonList(checked);
	}

}
