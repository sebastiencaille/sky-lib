package ch.skymarshall.dataflowmgr.examples.simple;

import ch.skymarshall.dataflowmgr.annotations.ExternalAdapter;
import ch.skymarshall.dataflowmgr.examples.simple.dto.MyData;

@ExternalAdapter
public class SimpleExternalAdapter {

	public String load(final MyData input) {
		switch (input.parameter) {
		case "Hello":
			return "World";
		case "Hi":
			return "there";
		default:
			throw new IllegalStateException("Unkown id: " + input);
		}
	}

	public void display(final MyData result) {
		System.out.println(result.output);
	}
}
