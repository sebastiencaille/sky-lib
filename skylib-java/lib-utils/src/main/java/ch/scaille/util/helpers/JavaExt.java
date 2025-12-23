package ch.scaille.util.helpers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;

import org.jetbrains.annotations.NotNull;

/**
 * 
 */
public class JavaExt {

	private JavaExt() {
		// noop
	}

	public static TimerTask timerTask(Runnable runnable) {
		return new TimerTask() {
			@Override
			public void run() {
				runnable.run();
			}
		};
	}

	/**
	 * Gets the path of a URI
	 */
	public static String pathOf(URI uri) {
		return pathOf(uri.getPath());
	}

	/**
	 * Gets the path of a URL
	 */
	public static String pathOf(URL url) {
		return pathOf(url.getPath());
	}

	private static String pathOf(String uriPath) {
		if (uriPath.length() > 2 && uriPath.charAt(2) == ':') {
			return uriPath.substring(1);
		}
		return uriPath;
	}

	public static void removeFolderUnsafe(Path path) {
		try {
			Files.walkFileTree(path, new SimpleFileVisitor<>() {
				@Override
				public @NotNull FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
					Files.delete(path);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public @NotNull FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					if (exc != null) {
						throw exc;
					}
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			Logs.of(JavaExt.class).log(Level.INFO, e, () -> "Cannot delete temp folder");
		}
	}

	public record AutoRemove(Path path) implements NoExceptionCloseable {

		@Override
		public void close() {
			removeFolderUnsafe(path);
		}
	}

	public static AutoRemove autoRemove(Path path) {
		return new AutoRemove(path);
	}

	public static class StreamHandler implements Runnable {

		private final Supplier<InputStream> in;
		private final Consumer<String> flow;

		public StreamHandler(Supplier<InputStream> in, final Consumer<String> flow) {
			this.in = in;
			this.flow = flow;
		}

		public void start() {
			new Thread(this).start();
		}

		@Override
		public void run() {
			// Use a reader to handle multibyte chars
			try (var reader = new InputStreamReader(in.get(), StandardCharsets.UTF_8)) {
				final var buffer = new char[1024 * 1024];
				int read;
				while ((read = reader.read(buffer, 0, buffer.length)) >= 0) {
					flow.accept(new String(buffer, 0, read));
				}
			} catch (final IOException e) {
				// ignore
			}
		}
	}

	/**
	 * To consume an input stream
	 */
	public static StreamHandler inputStreamHandler(Supplier<InputStream> in, final Consumer<String> flow) {
		return new StreamHandler(in, flow);
	}

	public interface AutoCloseableNoException extends AutoCloseable {
		@Override
		void close();
	}

	public static byte[] read(InputStream in) throws IOException {
		final var out = new ByteArrayOutputStream();
		in.transferTo(out);
		return out.toByteArray();
	}

	public static String readUTF8Stream(final InputStream in) throws IOException {
		final var inReader = new InputStreamReader(in, StandardCharsets.UTF_8);
		try (final var stringWriter = new StringWriter()) {
			inReader.transferTo(stringWriter);
			return stringWriter.toString();
		}
	}

	public static RuntimeException notImplemented() {
		return new IllegalStateException("Not implemented");
	}

	public static void failIfFalse(boolean flag, Supplier<RuntimeException> throwable) {
		if (!flag) {
			throw throwable.get();
		}
	}
}
