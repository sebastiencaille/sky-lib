package ch.scaille.testing.testpilot.selenium;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.openqa.selenium.NoSuchSessionException;

import ch.scaille.testing.testpilot.ModalDialogDetector;
import ch.scaille.testing.testpilot.ModalDialogDetector.PollingResult;

public class AlertDetector {

	private AlertDetector() {

	}

	/**
	 * Allows to run an action within a context that acknowledges the alerts and
     */
	public static void withAlert(final SeleniumPilot pilot, final Runnable runnable) {
		final var testThread = Thread.currentThread();
		final var dialogDetector = new ModalDialogDetector.Builder(() -> AlertDetector.listAlerts(pilot, null), e -> testThread.interrupt());
		try (var dialogEnabler = ModalDialogDetector.withModalDialogDetection(dialogDetector.build(pilot))) {
			runnable.run();
		}
	}

	public static List<ModalDialogDetector.PollingResult> listAlerts(final SeleniumPilot pilot,
			final Function<AlertPilot, PollingResult> errorChecks) {

		final var alertPilot = new AlertPilot(pilot);
		try {
			final var alert = alertPilot.loadGuiComponent();
			if (alert.isEmpty()) {
				return Collections.emptyList();
			}
			var pollingResult = ModalDialogDetector.notHandled("");
			if (errorChecks != null) {
				pollingResult = errorChecks.apply(alertPilot);
			}
			if (!pollingResult.handled) {
				final var existingAlert = alert.get();
				pollingResult = ModalDialogDetector.unhandled(existingAlert.getText(), existingAlert::accept);
			}
			return Collections.singletonList(pollingResult);
		} catch (NoSuchSessionException e) {
			// ignore
			return Collections.emptyList();
		}
	}

}
