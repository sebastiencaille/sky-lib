package ch.skymarshall.tcwriter.hmi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;
import ch.skymarshall.tcwriter.test.TestExecutionController;
import ch.skymarshall.tcwriter.test.TestExecutionController.Command;

public class TestRemoteControl {

	private final int baseTcpPort;

	private final Set<Integer> breakPoints = new HashSet<>();

	private Socket controlConnection = null;

	private int currentStep = -1;

	private BiConsumer<Integer, Integer> stepChangedListener;

	private ServerSocket controlServer;

	public TestRemoteControl(final int baseTcpPort) {
		this.baseTcpPort = baseTcpPort;
	}

	public void addBreakpoint(final TestStep testStep) {
		breakPoints.add(testStep.getOrdinal());
	}

	public void removeBreakpoint(final TestStep testStep) {
		breakPoints.remove(testStep.getOrdinal());
	}

	public Object hasBreakpoint(final TestStep testStep) {
		return breakPoints.contains(testStep.getOrdinal());
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

		controlServer.setSoTimeout(20000);
		controlConnection = controlServer.accept();
		Logger.getLogger(TCWriterHmi.class.getName()).log(Level.INFO, "Connected");
		TestExecutionController.handleCommands(controlConnection, (connection, command) -> {
			if (command == Command.STEP) {
				updateStep(TestExecutionController.readInt(connection.getInputStream()));
			}
		}, this::reset);

		for (final Integer breakPoint : breakPoints) {
			controlConnection.getOutputStream().write(TestExecutionController.Command.SET_BREAKPOINT.cmd);
			TestExecutionController.writeInt(controlConnection, breakPoint);
		}
		controlConnection.getOutputStream().write(TestExecutionController.Command.RUN.cmd);
	}

	public void reset() {
		updateStep(-1);
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

	public boolean isRunning(final int stepIndex) {
		return currentStep == stepIndex;
	}

	private void updateStep(final int newStep) {
		final int oldStep = currentStep;
		currentStep = newStep;
		stepChangedListener.accept(oldStep, currentStep);
	}

	public void setStepListener(final BiConsumer<Integer, Integer> stepChangedListener) {
		this.stepChangedListener = stepChangedListener;
	}

}