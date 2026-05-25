package ch.scaille.tcwriter.server.exceptions;

import org.springframework.http.HttpStatus;

public class DictionaryNotSetException extends WebRTException {
	
	public DictionaryNotSetException() {
		super(HttpStatus.CONFLICT, "dictionary.not.set");
	}

}
