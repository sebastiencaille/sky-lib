package ch.scaille.tcwriter.server.webapi.controllers.exceptions;

import org.springframework.http.HttpStatus;

import ch.scaille.tcwriter.server.web.controller.exceptions.WebRTException;

public class DictionaryNotFoundException extends WebRTException {
	
	public DictionaryNotFoundException(String resourceName) {
		super(HttpStatus.NOT_FOUND, "dictionary.not.found", resourceName);
	}

}
