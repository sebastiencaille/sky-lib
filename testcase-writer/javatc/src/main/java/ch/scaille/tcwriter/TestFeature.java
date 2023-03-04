package ch.scaille.tcwriter;

import java.io.IOException;
import java.util.logging.Logger;

import ch.scaille.tcwriter.model.persistence.FsModelDao;
import ch.scaille.tcwriter.recorder.TestCaseRecorder;
import ch.scaille.tcwriter.recorder.TestCaseRecorderAspect;
import ch.scaille.tcwriter.stepping.TestSteppingAspect;
import ch.scaille.tcwriter.stepping.TestSteppingController;

public abstract class TestFeature {

	private static final Logger LOGGER = Logger.getLogger(TestFeature.class.getName());

	public abstract void enable();

	private static TestFeature unavailable() {
		return new TestFeature() {

			@Override
			public void enable() {
				// nope
			}
		};
	}

	public static TestFeature aspectjStepping() {

		final var feature = new TestFeature() {
			@Override
			public void enable() {
				try {
					final TestSteppingController controller = new TestSteppingController();
					TestSteppingAspect.setController(controller);
				} catch (final IOException e) {
					throw new IllegalStateException("Unable to start stepping controller", e);
				}
			}
		};
		if (Boolean.getBoolean("tc.stepping")) {
			feature.enable();
		}
		return feature;
	}

}
