package ch.scaille.tcwriter.server.exceptions;

import org.springframework.http.HttpStatus;

public class DictionaryNotFoundException extends WebRTException {
	
	public DictionaryNotFoundException(String resourceName) {
		super(HttpStatus.NOT_FOUND, "dictionary.not.found", resourceName);
	}

}
