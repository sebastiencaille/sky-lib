package ch.scaille.tcwriter.pilot.selenium;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.openqa.selenium.NoSuchSessionException;

import ch.scaille.tcwriter.pilot.ModalDialogDetector;
import ch.scaille.tcwriter.pilot.ModalDialogDetector.PollingResult;

public class AlertDetector {

	private AlertDetector() {

	}

	public static void withAlert(final SeleniumPilot pilot, final Runnable runnable) {
		final var testThread = Thread.currentThread();
		final var dialogDetector = new ModalDialogDetector(() -> AlertDetector.listAlerts(pilot, null), e -> testThread.interrupt());
		try (var dialogEnabler = ModalDialogDetector.withModalDialogDetection(dialogDetector)) {
			runnable.run();
		}
	}

	public static List<ModalDialogDetector.PollingResult> listAlerts(final SeleniumPilot pilot,
			final Function<AlertPilot, PollingResult> errorChecks) {

		final var alertPilot = new AlertPilot(pilot);
		try {
			final var alert = alertPilot.loadGuiComponent();
			if (alert == null) {
				return Collections.emptyList();
			}
			var pollingResult = ModalDialogDetector.notHandled("");
			if (errorChecks != null) {
				pollingResult = errorChecks.apply(alertPilot);
			}
			if (!pollingResult.handled) {
				pollingResult = ModalDialogDetector.error(alert.getText(), alert::accept);
			}
			return Collections.singletonList(pollingResult);
		} catch (NoSuchSessionException e) {
			// ignore
			return Collections.emptyList();
		}
	}

}
