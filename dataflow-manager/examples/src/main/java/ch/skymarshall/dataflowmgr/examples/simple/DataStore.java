package ch.skymarshall.dataflowmgr.examples.simple;

import ch.skymarshall.dataflowmgr.annotations.ExternalInput;

@ExternalInput
public class DataStore {

	public String load(final int id) {
		switch (id) {
		case 1:
			return "One";
		case 2:
			return "Two";
		default:
			throw new IllegalStateException("Unkown id: " + id);
		}
	}

}
