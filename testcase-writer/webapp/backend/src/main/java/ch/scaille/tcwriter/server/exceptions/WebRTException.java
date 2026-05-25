package ch.scaille.tcwriter.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ServletRequestPathUtils;

public class WebRTException extends ResponseStatusException {
	
	public WebRTException(HttpStatus status, String reason, String... parameters) {
		super(status, reason, null, enhanceWithUrl(reason), parameters);
	}
	
	public WebRTException(Exception e) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, enhanceWithUrl(e.getMessage()), e);
	}

	private static String enhanceWithUrl(String message) {
		return RequestContextHolder.currentRequestAttributes().getAttribute(ServletRequestPathUtils.PATH_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST) + ": " + message;
	}
	
}
