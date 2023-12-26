package ch.scaille.tcwriter.server.webapi.service;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.session.SessionRepository;

import ch.scaille.tcwriter.server.WebConstants;
import ch.scaille.tcwriter.server.facade.WebFeedbackFacade;

public class WebSocketFeedbackFacade implements WebFeedbackFacade {

	private final SimpMessageSendingOperations feedbackSendingTemplate;

	private final SessionRepository<?> sessionRepository;

	public WebSocketFeedbackFacade(SessionRepository<?> sessionRepository,
			SimpMessageSendingOperations feedbackSendingTemplate) {
		this.sessionRepository = sessionRepository;
		this.feedbackSendingTemplate = feedbackSendingTemplate;
	}

	@Override
	public void send(Optional<String> sessionId, String destination, Object dto) {
		if (sessionId.isEmpty()) {
			return;
		}
		final var wsSessionId = sessionRepository.findById(sessionId.get())
				.getAttribute(WebConstants.SPRING_SESSION_WEBSOCKET_SESSION);
		if (wsSessionId == null) {
			return;
		}
		final var wsMessageWrapper = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
		wsMessageWrapper.setSessionId((String) wsSessionId);
		final var wsMessage = new GenericMessage<>(dto, wsMessageWrapper.getMessageHeaders());
		feedbackSendingTemplate.convertAndSendToUser((String) wsSessionId, destination, wsMessage, msg -> {
			final var headers = new HashMap<>(msg.getHeaders());
			headers.putAll(wsMessageWrapper.toMap());
			return new GenericMessage<>(msg.getPayload(), headers);
		});
	}

}
