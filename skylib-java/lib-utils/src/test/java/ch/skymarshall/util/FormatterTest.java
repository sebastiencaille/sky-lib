package ch.skymarshall.util;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import ch.skymarshall.util.text.ArrowIndentationManager;
import ch.skymarshall.util.text.SimpleTextFormatter;
import ch.skymarshall.util.text.StringListOutput;
import ch.skymarshall.util.text.TextFormatter;

public class FormatterTest {

	private static class ExceptionOutput implements TextFormatter.IOutput<IOException> {

		private final StringBuilder buffer = new StringBuilder();

		@Override
		public void append(String str) throws IOException {
			buffer.append(str);
			throw new IOException("Expected");
		}

		@Override
		public void append(char c) throws IOException {
			buffer.append(c);
		}

	}

	@Test(expected = IOException.class)
	public void testExceptionManagement() throws IOException {
		SimpleTextFormatter<IOException> tf = new SimpleTextFormatter<>(new ExceptionOutput());
		tf.append("Hello");
	}

	@Test
	public void testIndentation() {
		StringListOutput output = new StringListOutput();
		SimpleTextFormatter<RuntimeException> tf = new SimpleTextFormatter<>(output);
		tf.setIndentationManager(new ArrowIndentationManager());
		tf.appendIndentedLine("Hello");
		tf.indented(t -> t.appendIndentedLine("World"));
		tf.appendIndentedLine("Done");
		Assert.assertEquals(Arrays.asList("--> Hello", "    World", "--> Done"), output.getLines());
	}

}
