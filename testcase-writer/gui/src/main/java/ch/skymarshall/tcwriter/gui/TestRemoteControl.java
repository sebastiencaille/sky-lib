package ch.skymarshall.tcwriter.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;
import ch.skymarshall.tcwriter.test.TestExecutionController;
import ch.skymarshall.tcwriter.test.TestExecutionController.TestCaseError;

public class TestRemoteControl {

	public enum StepState {
		STARTED, OK, FAILED
	}

	public static class StepStatus {
		public final int ordinal;
		public boolean breakPoint = false;
		public StepState state = null;
		public String message;

		public StepStatus(final int ordinal) {
			this.ordinal = ordinal;
		}

		@Override
		public String toString() {
			return ordinal + "/bp=" + breakPoint + ",state=" + state + ",message=" + message;
		}
	}

	private final int baseTcpPort;

	private final Map<Integer, StepStatus> stepStates = new HashMap<>();

	private Socket controlConnection = null;

	private BiConsumer<Integer, Integer> stepChangedListener;

	private ServerSocket controlServer;

	public TestRemoteControl(final int baseTcpPort) {
		this.baseTcpPort = baseTcpPort;
	}

	public StepStatus stepStatus(final int ordinal) {
		return stepStates.computeIfAbsent(ordinal, o -> new StepStatus(o));
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
		cleanSteps();
		controlServer.setSoTimeout(20_000);
		controlConnection = controlServer.accept();
		Logger.getLogger(TCWriterGui.class.getName()).log(Level.INFO, "Connected");
		TestExecutionController.handleCommands(controlConnection, (connection, command) -> {
			try (final InputStream inputStream = controlConnection.getInputStream()) {
				switch (command) {
				case STEP_START:
					final int startStepNumber = TestExecutionController.readStepNumber(inputStream);
					stepStatus(startStepNumber).state = StepState.STARTED;
					stepChangedListener.accept(startStepNumber, startStepNumber);
					break;
				case STEP_DONE:
					final int stopStepNumber = TestExecutionController.readStepNumber(inputStream);
					final StepStatus stepStatus = stepStatus(stopStepNumber);
					if (stepStatus.state == StepState.STARTED) {
						stepStatus.state = StepState.OK;
					}
					stepChangedListener.accept(stopStepNumber, stopStepNumber);
					break;
				case ERROR:
					final TestCaseError errorMessage = TestExecutionController.readErrorMessage(inputStream);
					final int errStepNumber = errorMessage.stepNumber;
					stepStatus(errStepNumber).state = StepState.FAILED;
					stepStatus(errStepNumber).message = errorMessage.message;
					stepChangedListener.accept(errStepNumber, errStepNumber);
					break;
				default:
					break;
				}
			}
		}, this::resetConnection);

		stepStates.values().stream().filter(s -> s.breakPoint).forEach(s -> {
			try {
				controlConnection.getOutputStream().write(TestExecutionController.Command.SET_BREAKPOINT.cmd);
				TestExecutionController.writeInt(controlConnection, s.ordinal);
			} catch (final IOException e) {
				throw new IllegalStateException("Unable to setup connection", e);
			}
		});
		controlConnection.getOutputStream().write(TestExecutionController.Command.RUN.cmd);
	}

	public void cleanSteps() {
		stepStates.values().forEach(s -> s.state = null);
		stepChangedListener.accept(1, Integer.MAX_VALUE);
	}

	public void resetConnection() {
		if (controlConnection != null) {
			try {
				controlConnection.close();
			} catch (final IOException e) {
				// ignore
			}
			controlConnection = null;
		}

	}

	public void resume() throws IOException {
		controlConnection.getOutputStream().write(TestExecutionController.Command.RUN.cmd);
	}

	public void setStepListener(final BiConsumer<Integer, Integer> stepChangedListener) {
		this.stepChangedListener = stepChangedListener;
	}

}