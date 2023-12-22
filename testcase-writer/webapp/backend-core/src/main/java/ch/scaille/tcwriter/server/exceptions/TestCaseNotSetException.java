package ch.scaille.tcwriter.server.exceptions;

import org.springframework.http.HttpStatus;

public class TestCaseNotSetException extends WebRTException {
	
	public TestCaseNotSetException() {
		super(HttpStatus.CONFLICT, "testcase.not.set");
	}

}
