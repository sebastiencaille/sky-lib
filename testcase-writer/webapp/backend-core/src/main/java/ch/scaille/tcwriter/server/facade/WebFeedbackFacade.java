package ch.scaille.tcwriter.server.facade;

import java.util.Optional;

public interface WebFeedbackFacade {

	void send(Optional<String> wsSessionId, String tabId, String destination, Object dto);

}
