package ch.scaille.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.scaille.util.text.ArrowIndentationManager;
import ch.scaille.util.text.SimpleTextFormatter;
import ch.scaille.util.text.StringListOutput;
import ch.scaille.util.text.TextFormatter;

class FormatterTest {

	private static class ExceptionOutput implements TextFormatter.IOutput<IOException> {

		private final StringBuilder buffer = new StringBuilder();

		@Override
		public void append(String str) throws IOException {
			buffer.append(str);
			throw new IOException("Expected");
		}

		@Override
		public void append(char c) {
			buffer.append(c);
		}

	}

	@Test
	void testExceptionManagement() {
		final var tf = new SimpleTextFormatter<>(new ExceptionOutput());
		assertThrows(IOException.class, () -> tf.append("Hello"));
	}

	@Test
	void testIndentation() {
		final var  output = new StringListOutput();
		final var tf = new SimpleTextFormatter<>(output);
		tf.setIndentationManager(new ArrowIndentationManager());
		tf.appendIndentedLine("Hello");
		tf.indented(t -> t.appendIndentedLine("World"));
		tf.appendIndentedLine("Done");
		assertEquals(Arrays.asList("--> Hello", "    World", "--> Done"), output.getLines());
	}

}
