package ch.scaille.tcwriter.server.web.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class WebRTException extends ResponseStatusException {
	
	public WebRTException(HttpStatus status, String code, String... parameters) {
		super(status, code, null, code, parameters);
	}
	
	public WebRTException(Exception e) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
	}

}
