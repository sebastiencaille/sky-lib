package ch.scaille.tcwriter.server.facade;

import org.jspecify.annotations.Nullable;

public interface WebFeedbackFacade {

	void send(@Nullable String wsSessionId, String tabId, String destination, Object dto);

}
