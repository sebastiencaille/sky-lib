package ch.scaille.tcwriter.server.exceptions;

import org.springframework.http.HttpStatus;

public class TestCaseNotFoundException extends WebRTException {
	
	public TestCaseNotFoundException(String resourceName) {
		super(HttpStatus.NOT_FOUND, "testcase.not.found", resourceName);
	}

}
