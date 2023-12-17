package ch.scaille.tcwriter.services.testexec;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.scaille.tcwriter.model.testcase.TestStep;
import ch.scaille.tcwriter.model.testexec.StepStatus;
import ch.scaille.tcwriter.services.testexec.TestApi.StepState;
import ch.scaille.util.helpers.Logs;

public class TestRemoteControl {

	private static final Logger LOGGER = Logs.of(TestRemoteControl.class);

	private final int baseTcpPort;

	private final TestExecutionListener testExecutionListener;

	private final Map<Integer, StepStatus> stepStates = new HashMap<>();

	private Socket controlConnection = null;

	private BiConsumer<Integer, Integer> stepChangedListener;

	private ServerSocket controlServer;

	private TestApi api;


	public TestRemoteControl(int baseTcpPort, TestExecutionListener testExecutionListener) {
		this.baseTcpPort = baseTcpPort;
		this.testExecutionListener = testExecutionListener;
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
		var controlPort = baseTcpPort;
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

	public void controlTest(int stepsCount) throws IOException {
		testExecutionListener.testRunning(true);
		cleanSteps(stepsCount);
		controlServer.setSoTimeout(20_000);
		try {
			controlConnection = controlServer.accept();
			LOGGER.log(Level.INFO, "Connected");
			api = new TestApi(controlConnection);
			TestApi.handleCommands(api, command -> {

				switch (command) {
				case STEP_START:
					final var startStepNumber = api.readStartBody();
					final var startStatus = stepStatus(startStepNumber);
					startStatus.state = StepState.STARTED;
					stepChangedListener.accept(startStepNumber, startStepNumber);
					if (startStatus.breakPoint) {
						testExecutionListener.testPaused(true);
					}
					break;
				case STEP_DONE:
					final var stopStepNumber = api.readDoneBody();
					final var stopStepStatus = stepStatus(stopStepNumber);
					if (stopStepStatus.state == StepState.STARTED) {
						stopStepStatus.state = StepState.OK;
					}
					testExecutionListener.testPaused(false);
					stepChangedListener.accept(stopStepNumber, stopStepNumber);
					break;
				case ERROR:
					final var errorMessage = api.readErrorBody();
					final var errStepNumber = errorMessage.stepNumber;
					final var errStepStatus = stepStatus(errStepNumber);
					errStepStatus.state = StepState.FAILED;
					errStepStatus.message = errorMessage.message;
					stepChangedListener.accept(errStepNumber, errStepNumber);
					break;
				default:
					break;
				}
			}, this::resetConnection, () -> {
				testExecutionListener.testRunning(false);
				testExecutionListener.testFinished();
			});

			stepStates.values().stream().filter(s -> s.breakPoint).forEach(api::setBreakPoint);
			api.write(TestApi.Command.RUN);
		} catch (IOException e) {
			testExecutionListener.testRunning(false);
			throw e;
		} finally {
			controlServer.close();
		}
	}

	public void cleanSteps(int stepsCount) {
		stepStates.clear();
		if (stepChangedListener != null) {
			stepChangedListener.accept(1, stepsCount);
		}
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

	/**
	 *
	 * @param stepChangedListener listen to first and last changed step
	 */
	public void setStepListener(final BiConsumer<Integer, Integer> stepChangedListener) {
		this.stepChangedListener = stepChangedListener;
	}

}