package ch.scaille.util.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

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

	static Writer streamOf(Class<?> clazz, Level level) {
		final var logger = Logs.of(clazz);
		return new Writer() {

			private StringBuilder builder = new StringBuilder();

			@Override
			public void write(char @NotNull [] cbuf, int off, int len) {
				builder.append(cbuf, off, len);
			}

			@Override
			public void flush() {
				if (logger.isLoggable(level)) {
					logger.log(level, builder.toString());
				}
				builder = new StringBuilder();
			}

			@Override
			public void close() {
				flush();
			}

		};
	}

	static void info(Logger logger, Supplier<String> message, Exception e) {
		if (logger.isLoggable(Level.INFO)) {
			logger.info(message.get());
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.log(Level.FINE, message.get(), e);
		}
	}

	static void configureFromClassPath() throws SecurityException, IOException {
		try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("logging.properties")) {
			LogManager.getLogManager().readConfiguration(in);
		}
	}

}
