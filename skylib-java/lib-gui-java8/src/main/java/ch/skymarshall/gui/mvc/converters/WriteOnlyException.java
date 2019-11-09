package ch.skymarshall.gui.mvc.converters;

public class WriteOnlyException extends RuntimeException {

	public WriteOnlyException() {
		super("Write only converter");
	}
}
