package ch.scaille.javabeans.converters;

public class WriteOnlyException extends RuntimeException {

	public WriteOnlyException() {
		super("Write only converter");
	}
}
