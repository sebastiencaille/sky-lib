package ch.skymarshall.tcwriter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;

import ch.skymarshall.tcwriter.recording.ITestCaseRecorder;
import ch.skymarshall.tcwriter.recording.TestCaseRecorderAspect;
import ch.skymarshall.tcwriter.stepping.TestSteppingAspect;
import ch.skymarshall.tcwriter.stepping.TestSteppingController;

public abstract class TestFeature {

	private static final String RECORDER_CLASS = "ch.skymarshall.tcwriter.generators.recorder.TestCaseRecorder";

	public abstract void enable();

	private static TestFeature unavailable() {
		return new TestFeature() {

			@Override
			public void enable() {
				// nope
			}
		};
	}

	public static TestFeature aspectjRecorder() {
		final String jsonModelPath = System.getProperty("tc.jsonModelFile");
		if (jsonModelPath == null) {
			return unavailable();
		}
		final Class<?> recorderClass;
		try {
			recorderClass = Class.forName(RECORDER_CLASS);
		} catch (final ClassNotFoundException e) {
			// Feature not available
			return unavailable();
		}
		try {
			final ITestCaseRecorder recorder = (ITestCaseRecorder) recorderClass.getConstructor(Path.class)
					.newInstance(Paths.get(jsonModelPath));
			return new TestFeature() {
				@Override
				public void enable() {
					TestCaseRecorderAspect.setRecorder(recorder);
				}
			};
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new IllegalStateException("Unable to load recorder", e);
		}
	}

	public static TestFeature aspectjStepping() {

		final TestFeature feature = new TestFeature() {
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
