package ch.scaille.tcwriter.server.webapi.controllers.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import ch.scaille.tcwriter.server.web.controller.exceptions.WebRTException;

public class TestCaseNotSetException extends WebRTException {
	
	public TestCaseNotSetException() {
		super(HttpStatus.CONFLICT, "testcase.not.set");
	}

}
