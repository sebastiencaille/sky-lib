package ch.scaille.tcwriter.stepping;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import ch.scaille.tcwriter.stepping.TestSteppingController.CommandHandler;
import ch.scaille.tcwriter.stepping.TestSteppingController.TestCaseError;

public class TestApi {
	public enum StepState {
		STARTED, OK, FAILED
	}

	public enum Command {
		SET_BREAKPOINT('b'), REMOVE_BREAKPOINT('c'), RUN('r'), STEP_START('s'), STEP_DONE('o'), ERROR('e'),
		EXIT((char) 255);

		public final char cmd;

		private Command(final char cmd) {
			this.cmd = cmd;
		}

		public static Command from(final int cmd) {
			for (final var command : Command.values()) {
				if (command.cmd == (char) (cmd & 0xFF)) {
					return command;
				}
			}
			throw new IllegalArgumentException("No command for " + cmd);
		}
	}

	private final InputStream inputStream;
	private final OutputStream outputStream;

	public TestApi(final Socket socket) throws IOException {
		this.inputStream = new BufferedInputStream(socket.getInputStream());
		this.outputStream = socket.getOutputStream();
	}

	public Command readCommand() throws IOException {
		return Command.from(inputStream.read());
	}

	public int readStart() throws IOException {
		return readStepNumber();
	}

	public int readDone() throws IOException {
		return readStepNumber();
	}

	public TestCaseError readErrorMessage() throws IOException {
		return new TestCaseError(readInt(), readString(), readString());
	}

	public int readBreakpoint() throws IOException {
		return readInt();
	}

	public void setBreakPoint(final StepStatus s) {
		try {
			write(Command.SET_BREAKPOINT);
			writeInt(s.ordinal);
		} catch (final IOException e) {
			throw new IllegalStateException("Unable to setup connection", e);
		}
	}

	private int readStepNumber() throws IOException {
		return readInt();
	}

	private int readInt() throws IOException {
		final int high = inputStream.read();
		final int low = inputStream.read();
		return ((high & 0xFF) << 8) | (low & 0xFF);
	}

	public void write(final Command command) throws IOException {
		outputStream.write(command.cmd);
	}

	private void writeInt(final int value) throws IOException {
		outputStream.write(value >> 8 & 0xFF);
		outputStream.write(value & 0xFF);
	}

	private String readString() throws IOException {
		final int length = readInt();
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

	public void writeString(final String message) throws IOException {
		writeInt(message.length());
		outputStream.write(message.getBytes(StandardCharsets.UTF_8));
	}

	public void writeStepCommand(final Command command, final int index) throws IOException {
		write(command);
		writeInt(index);
	}

	public void writeError(final int currentStep, final Throwable error, final StringWriter errorStack)
			throws IOException {
		outputStream.write(Command.ERROR.cmd);
		writeInt(currentStep);
		writeString(error.getMessage());
		writeString(errorStack.toString());
	}

	public static void handleCommands(final TestApi api, final CommandHandler commandHandler,
			final Runnable disconnectionHandler, final Runnable testFinished) {
		new Thread(() -> {
			try {
				Command receivedCommand;
				while ((receivedCommand = api.readCommand()) != Command.EXIT) {
					commandHandler.execute(receivedCommand);
				}
			} catch (final IOException e) {
				// ignore
			} finally {
				if (disconnectionHandler != null) {
					disconnectionHandler.run();
				}
				testFinished.run();
			}
		}).start();
	}

}
