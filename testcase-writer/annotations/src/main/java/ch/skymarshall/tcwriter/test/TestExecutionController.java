package ch.skymarshall.tcwriter.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class TestExecutionController {

	public enum Command {
		SET_BREAKPOINT('b'), REMOVE_BREAKPOINT('c'), RUN('r'), STEP_START('s'), STEP_OK('o'), TEST_FAILED('f'),
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

	private void commandHandler(final Socket connection, final Command command) throws IOException {
		switch (command) {
		case RUN:
			pauseSemaphore.release();
			break;
		case SET_BREAKPOINT:
			breakpoints.add(readInt(connection.getInputStream()));
			break;
		case REMOVE_BREAKPOINT:
			breakpoints.remove(readInt(connection.getInputStream()));
			break;
		default:
			break;
		}
	}

	public void beforeTestExecution() throws InterruptedException {
		if (remoteControlConnection != null) {
			pauseSemaphore.acquire();
		}
	}

	public void beforeStepExecution(final int index) throws InterruptedException {
		writeStep(index, Command.STEP_START);
		if (breakpoints.contains(index)) {
			pauseSemaphore.acquire();
		}
	}

	public void afterStepExecution(final int index) {
		writeStep(index, Command.STEP_OK);
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

	@FunctionalInterface
	public interface CommandHandler {
		void execute(Socket connection, Command command) throws IOException;
	}

	public static void handleCommands(final Socket connection, final CommandHandler commandHandler,
			final Runnable disconnectionHandler) {
		new Thread(() -> {
			try (InputStream inputStream = connection.getInputStream()) {
				Command receivedCommand;
				while ((receivedCommand = Command.from(inputStream.read())) != Command.EXIT) {
					commandHandler.execute(connection, receivedCommand);
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

	public static int readStepNumber(final Socket connection) throws IOException {
		return readInt(connection.getInputStream());
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

}
