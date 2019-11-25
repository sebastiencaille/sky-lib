package ch.skymarshall.tcwriter.gui;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;
import ch.skymarshall.tcwriter.stepping.StepStatus;
import ch.skymarshall.tcwriter.stepping.TestApi;
import ch.skymarshall.tcwriter.stepping.TestApi.StepState;
import ch.skymarshall.tcwriter.stepping.TestSteppingController.TestCaseError;

public class TestRemoteControl {

	private static final Logger LOGGER = Logger.getLogger(TCWriterGui.class.getName());

	private final int baseTcpPort;

	private final Consumer<Boolean> testRunning;

	private final Consumer<Boolean> testPaused;

	private final Map<Integer, StepStatus> stepStates = new HashMap<>();

	private Socket controlConnection = null;

	private BiConsumer<Integer, Integer> stepChangedListener;

	private ServerSocket controlServer;

	private TestApi api;

	public TestRemoteControl(final int baseTcpPort, final Consumer<Boolean> testRunning,
			final Consumer<Boolean> testPaused) {
		this.baseTcpPort = baseTcpPort;
		this.testRunning = testRunning;
		this.testPaused = testPaused;
	}

	public StepStatus stepStatus(final int ordinal) {
		return stepStates.computeIfAbsent(ordinal, StepStatus::new);
	}

	public void addBreakpoint(final TestStep testStep) {
		stepStatus(testStep.getOrdinal()).breakPoint = true;
	}

	public void removeBreakpoint(final TestStep testStep) {
		stepStatus(testStep.getOrdinal()).breakPoint = false;
	}

	public int prepare() {
		controlServer = null;
		int controlPort = baseTcpPort;
		while (controlServer == null && controlPort < baseTcpPort + 10) {
			try {
				controlServer = new ServerSocket(controlPort);
			} catch (final IOException e) {
				controlPort++;
			}
		}

		if (controlServer == null) {
			throw new IllegalStateException("Unable to open control port");
		}
		return controlPort;
	}

	public void start() throws IOException {
		testRunning.accept(true);
		cleanSteps();
		controlServer.setSoTimeout(20_000);
		controlConnection = controlServer.accept();
		LOGGER.log(Level.INFO, "Connected");
		api = new TestApi(controlConnection);
		TestApi.handleCommands(api, command -> {

			switch (command) {
			case STEP_START:
				final int startStepNumber = api.readStart();
				final StepStatus startStatus = stepStatus(startStepNumber);
				startStatus.state = StepState.STARTED;
				stepChangedListener.accept(startStepNumber, startStepNumber);
				if (startStatus.breakPoint) {
					testPaused.accept(true);
				}
				break;
			case STEP_DONE:
				final int stopStepNumber = api.readDone();
				final StepStatus stopStepStatus = stepStatus(stopStepNumber);
				if (stopStepStatus.state == StepState.STARTED) {
					stopStepStatus.state = StepState.OK;
				}
				testPaused.accept(false);
				stepChangedListener.accept(stopStepNumber, stopStepNumber);
				break;
			case ERROR:
				final TestCaseError errorMessage = api.readErrorMessage();
				final int errStepNumber = errorMessage.stepNumber;
				final StepStatus errStepStatus = stepStatus(errStepNumber);
				errStepStatus.state = StepState.FAILED;
				errStepStatus.message = errorMessage.message;
				stepChangedListener.accept(errStepNumber, errStepNumber);
				break;
			default:
				break;
			}
		}, this::resetConnection, () -> testRunning.accept(false));

		stepStates.values().stream().filter(s -> s.breakPoint).forEach(api::setBreakPoint);
		api.write(TestApi.Command.RUN);
	}

	public void cleanSteps() {
		stepStates.values().forEach(s -> s.state = null);
		stepChangedListener.accept(1, Integer.MAX_VALUE);
	}

	public void resetConnection() {
		LOGGER.log(Level.INFO, "Disconnected");
		if (controlConnection != null) {
			try {
				controlConnection.close();
			} catch (final IOException e) {
				// ignore
			}
			api = null;
			controlConnection = null;
		}

	}

	public void resume() throws IOException {
		api.write(TestApi.Command.RUN);
	}

	public void setStepListener(final BiConsumer<Integer, Integer> stepChangedListener) {
		this.stepChangedListener = stepChangedListener;
	}

}