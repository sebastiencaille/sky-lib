package ch.scaille.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.scaille.util.text.ArrowIndentationManager;
import ch.scaille.util.text.SimpleTextFormatter;
import ch.scaille.util.text.StringListOutput;
import ch.scaille.util.text.TextFormatter;

class FormatterTest {

	private static class ExceptionOutput implements TextFormatter.IOutput<IOException> {

		@Override
		public void append(String str) throws IOException {
			throw new IOException("Expected");
		}

		@Override
		public void append(char c) {
			// noop
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
		assertEquals(List.of("--> Hello", "    World", "--> Done"), output.getLines());
	}

}
