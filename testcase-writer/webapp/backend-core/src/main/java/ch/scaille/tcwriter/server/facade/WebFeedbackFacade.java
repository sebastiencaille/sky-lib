package ch.scaille.tcwriter.server.facade;

public interface WebFeedbackFacade {

	void send(String wsSessionId, String tabId, String destination, Object dto);

}
