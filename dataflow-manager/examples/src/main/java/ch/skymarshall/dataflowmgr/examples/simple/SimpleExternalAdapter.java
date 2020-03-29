package ch.skymarshall.dataflowmgr.examples.simple;

import ch.skymarshall.dataflowmgr.annotations.ExternalAdapters;
import ch.skymarshall.dataflowmgr.examples.simple.dto.MyData;

@ExternalAdapters
public class SimpleExternalAdapter {

	public String enhancement(final MyData input) {
		switch (input.parameter) {
		case "Hello":
			return "World";
		case "Hi":
			return "There";
		default:
			throw new IllegalStateException("Unkown id: " + input);
		}
	}

	public void display(final MyData result) {
		System.out.println(result.output);
	}
}
