package ch.scaille.util.helpers;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
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
	 * Get the path of an URI
	 * 
	 * @param uri
	 * @return
	 */
	public static String pathOf(URI uri) {
		return pathOf(uri.getPath());
	}

	/**
	 * Get the path from an URL
	 * 
	 * @param uri
	 * @return
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
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
					Files.delete(path);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
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

	public static class AutoRemove implements NoExceptionCloseable {
		public final Path path;

		public AutoRemove(Path path) {
			this.path = path;
		}

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
			// Use a reader to handle multi-byte chars
			try (Reader reader = new InputStreamReader(in.get(), StandardCharsets.UTF_8)) {
				final char[] buffer = new char[1024 * 1024];
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
	 * 
	 * @param in
	 * @param flow
	 * @return
	 */
	public static StreamHandler inputStreamHandler(Supplier<InputStream> in, final Consumer<String> flow) {
		return new StreamHandler(in, flow);
	}

	public interface AutoCloseableNoException extends AutoCloseable {
		@Override
		void close();
	}

	/**
	 * @deprecated use java 9 transferTo
	 */
	@Deprecated
	public static <T extends OutputStream> T transferTo(InputStream in, T out) throws IOException {
		final byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) > 0) {
			out.write(buffer, 0, read);
		}
		return out;
	}

	public static byte[] read(InputStream in) throws IOException {
		return transferTo(in, new ByteArrayOutputStream()).toByteArray();
	}

	/**
	 * @deprecated use java 9 transferTo
	 */
	@Deprecated
	public static <T extends Writer> T transferTo(Reader in, T out) throws IOException {
		final char[] buffer = new char[1024];
		int read;
		while ((read = in.read(buffer)) > 0) {
			out.write(buffer, 0, read);
		}
		return out;
	}
	
	public static void transferUTF8LineTo(InputStream in, Consumer<String> out) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
			String str;
			while ((str = reader.readLine()) != null) {
				out.accept(str);
			}
		}
	}

	public static String readUTF8Stream(final InputStream in) throws IOException {
		InputStreamReader inReader = new InputStreamReader(in, StandardCharsets.UTF_8);
		try (final StringWriter stringWriter = new StringWriter()) {
			return JavaExt.transferTo(inReader, stringWriter).toString();
		}
	}

	public static RuntimeException notImplemented() {
		return new IllegalStateException("Not implemented");
	}

}
