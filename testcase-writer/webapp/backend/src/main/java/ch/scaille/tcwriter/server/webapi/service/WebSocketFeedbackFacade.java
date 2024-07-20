package ch.scaille.tcwriter.server.webapi.service;

import java.util.HashMap;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.GenericMessage;

import ch.scaille.tcwriter.server.facade.WebFeedbackFacade;

public class WebSocketFeedbackFacade implements WebFeedbackFacade {

	private final SimpMessageSendingOperations feedbackSendingTemplate;

	public WebSocketFeedbackFacade(SimpMessageSendingOperations feedbackSendingTemplate) {
		this.feedbackSendingTemplate = feedbackSendingTemplate;
	}

	@Override
	public void send(String wsSessionId, String tabId, String destination, Object dto) {
		if (wsSessionId.isEmpty() || tabId == null) {
			return;
		}

		final var wsMessageWrapper = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
		wsMessageWrapper.setSessionId(wsSessionId);
		final var wsMessage = new GenericMessage<>(dto, wsMessageWrapper.getMessageHeaders());
		feedbackSendingTemplate.convertAndSendToUser(wsSessionId, destination, wsMessage, msg -> {
			final var headers = new HashMap<>(msg.getHeaders());
			headers.putAll(wsMessageWrapper.toMap());
			return new GenericMessage<>(msg.getPayload(), headers);
		});

	}

}
