package ch.scaille.util.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;

public class FilesExt {

	private FilesExt() {
		// noop
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
			Logs.of(FilesExt.class).log(Level.INFO, e, () -> "Cannot delete temp folder");
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
			try (InputStream strIn = in.get()) {
				final byte[] buffer = new byte[1024 * 1024];
				int read;
				while ((read = strIn.read(buffer, 0, buffer.length)) >= 0) {
					flow.accept(new String(buffer, 0, read));
				}
			} catch (final IOException e) {
				// ignore
			}
		}
	}

	public static StreamHandler streamHandler(Supplier<InputStream> in, final Consumer<String> flow) {
		return new StreamHandler(in, flow);
	}

}
