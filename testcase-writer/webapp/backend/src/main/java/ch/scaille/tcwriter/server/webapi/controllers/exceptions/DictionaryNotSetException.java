package ch.scaille.tcwriter.server.webapi.controllers.exceptions;

import org.springframework.http.HttpStatus;

import ch.scaille.tcwriter.server.web.controller.exceptions.WebRTException;

public class DictionaryNotSetException extends WebRTException {
	
	public DictionaryNotSetException() {
		super(HttpStatus.CONFLICT, "dictionary.not.set");
	}

}
