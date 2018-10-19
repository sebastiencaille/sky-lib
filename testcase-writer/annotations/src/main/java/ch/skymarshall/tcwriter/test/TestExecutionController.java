package ch.skymarshall.tcwriter.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class TestExecutionController implements ITestExecutionController {

	public enum Command {
		SET_BREAKPOINT('b'), REMOVE_BREAKPOINT('c'), RUN('r'), STEP_START('s'), STEP_DONE('o'), ERROR('e'),
		EXIT((char) 255);
		public final char cmd;

		private Command(final char cmd) {
			this.cmd = cmd;
		}

		public static Command from(final int cmd) {
			for (final Command command : Command.values()) {
				if (command.cmd == (char) (cmd & 0xFF)) {
					return command;
				}
			}
			throw new IllegalArgumentException("No command for " + cmd);
		}
	}

	private final Semaphore pauseSemaphore = new Semaphore(0);

	private final Set<Integer> breakpoints = new HashSet<>();

	private final Socket remoteControlConnection;

	private int currentStep;

	public static ITestExecutionController controller() throws IOException {
		if (Boolean.getBoolean("remote.controller")) {
			return new TestExecutionController();
		} else {
			return new ITestExecutionController() {

				@Override
				public void beforeTestExecution() {
					// dummy
				}

				@Override
				public void beforeStepExecution(final int i) {
					// dummy
				}

				@Override
				public void afterStepExecution(final int i) {
					// dummy
				}

				@Override
				public void notifyError(final Throwable error) {
					// dummy
				}
			};
		}
	}

	public TestExecutionController() throws IOException {
		final String host = System.getProperty("test.host", "127.0.0.1");
		final Integer tcpPort = Integer.getInteger("test.port");
		if (tcpPort != null) {
			final InetSocketAddress remoteControlAddress = new InetSocketAddress(host, tcpPort);
			System.out.println("Connecting to " + remoteControlAddress);

			remoteControlConnection = new Socket();
			int retry = 5;
			while (!remoteControlConnection.isConnected() && retry > 0) {
				try {
					remoteControlConnection.connect(remoteControlAddress, 5000);
				} catch (final SocketTimeoutException e) {
					retry--;
				}
			}
			if (!remoteControlConnection.isConnected()) {
				throw new IllegalStateException("Cannot connect to remote control: " + remoteControlAddress);
			}
			handleCommands(remoteControlConnection, this::commandHandler, () -> System.exit(1));
		} else {
			remoteControlConnection = null;
		}
	}

	private void commandHandler(final InputStream inputStream, final Command command) throws IOException {
		switch (command) {
		case RUN:
			pauseSemaphore.release();
			break;
		case SET_BREAKPOINT:
			breakpoints.add(readInt(inputStream));
			break;
		case REMOVE_BREAKPOINT:
			breakpoints.remove(readInt(inputStream));
			break;
		default:
			break;
		}
	}

	@Override
	public void beforeTestExecution() throws InterruptedException {
		if (remoteControlConnection != null) {
			pauseSemaphore.acquire();
		}
	}

	@Override
	public void beforeStepExecution(final int index) throws InterruptedException {
		writeStep(index, Command.STEP_START);
		if (breakpoints.contains(index)) {
			pauseSemaphore.acquire();
		}
		currentStep = index;
	}

	@Override
	public void afterStepExecution(final int index) {
		writeStep(index, Command.STEP_DONE);
	}

	private void writeStep(final int index, final Command command) {
		if (remoteControlConnection != null) {
			try {
				remoteControlConnection.getOutputStream().write(command.cmd);
				writeInt(remoteControlConnection, index);
			} catch (final IOException e) {
				// ignore
			}
		}
	}

	@Override
	public void notifyError(final Throwable error) {

		try (StringWriter errorStack = new StringWriter()) {
			error.printStackTrace(new PrintWriter(errorStack));

			remoteControlConnection.getOutputStream().write(Command.ERROR.cmd);
			writeInt(remoteControlConnection, currentStep);
			writeString(remoteControlConnection, error.getMessage());
			writeString(remoteControlConnection, errorStack.toString());
		} catch (final IOException e) {
			System.out.println("Unable to send error: " + e.getMessage());
		}
	}

	@FunctionalInterface
	public interface CommandHandler {
		void execute(InputStream inputStream, Command command) throws IOException;
	}

	public static void handleCommands(final Socket connection, final CommandHandler commandHandler,
			final Runnable disconnectionHandler) {
		new Thread(() -> {
			try (InputStream inputStream = connection.getInputStream()) {
				Command receivedCommand;
				while ((receivedCommand = Command.from(inputStream.read())) != Command.EXIT) {
					commandHandler.execute(inputStream, receivedCommand);
				}
			} catch (final IOException e) {
				// ignore
			} finally {
				if (disconnectionHandler != null) {
					disconnectionHandler.run();
				}
			}
		}).start();
	}

	public static int readStepNumber(final InputStream inputStream) throws IOException {
		return readInt(inputStream);
	}

	public static int readInt(final InputStream inputStream) throws IOException {
		final int high = inputStream.read();
		final int low = inputStream.read();
		return ((high & 0xFF) << 8) | (low & 0xFF);
	}

	public static void writeInt(final Socket connection, final int value) throws IOException {
		connection.getOutputStream().write(value >> 8 & 0xFF);
		connection.getOutputStream().write(value & 0xFF);
	}

	public static String readString(final InputStream inputStream) throws IOException {
		final int length = readInt(inputStream);
		final byte[] bytes = new byte[length];
		int total = 0;
		while (total != length) {
			final int read = inputStream.read(bytes, total, length - total);
			if (read == -1) {
				throw new IllegalStateException("Unfinished string");
			}
			total += read;
		}
		return new String(bytes, StandardCharsets.UTF_8);
	}

	public static void writeString(final Socket connection, final String message) throws IOException {
		System.err.println("Writing message of length " + message.length());
		writeInt(connection, message.length());
		connection.getOutputStream().write(message.getBytes(StandardCharsets.UTF_8));
	}

	public static class TestCaseError {
		public final int stepNumber;
		public final String message;
		public final String stack;

		public TestCaseError(final int step, final String message, final String stack) {
			this.stepNumber = step;
			this.message = message;
			this.stack = stack;
		}

	}

	public static TestCaseError readErrorMessage(final InputStream inputStream) throws IOException {
		return new TestCaseError(readInt(inputStream), readString(inputStream), readString(inputStream));
	}

}
