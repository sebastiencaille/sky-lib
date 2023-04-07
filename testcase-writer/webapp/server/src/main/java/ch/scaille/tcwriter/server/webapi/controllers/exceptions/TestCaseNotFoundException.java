package ch.scaille.tcwriter.server.webapi.controllers.exceptions;

import org.springframework.http.HttpStatus;

import ch.scaille.tcwriter.server.web.controller.exceptions.WebRTException;

public class TestCaseNotFoundException extends WebRTException {
	
	public TestCaseNotFoundException(String resourceName) {
		super(HttpStatus.NOT_FOUND, "testcase.not.found", resourceName);
	}

}
