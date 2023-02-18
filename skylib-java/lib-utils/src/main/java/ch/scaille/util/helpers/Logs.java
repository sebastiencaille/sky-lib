package ch.scaille.util.helpers;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface Logs {

	static Logger of(Class<?> clazz) {
		return Logger.getLogger(clazz.getName());
	}

	static Logger of(String logger) {
		return Logger.getLogger(logger);
	}

	static Logger of(Object obj) {
		return of(obj.getClass());
	}

	static OutputStream streamOf(Class<?> clazz, Level level) {
		final Logger logger = Logs.of(clazz);
		return new OutputStream() {

			private StringBuilder builder = new StringBuilder();

			@Override
			public void write(int b) {
				builder.append((char) b);
			}

			@Override
			public void flush() throws IOException {
				super.flush();
				logger.log(level, () -> builder.toString());
				builder = new StringBuilder();
			}

			@Override
			public void close() throws IOException {
				flush();
				super.close();
			}
		};
	}

}
