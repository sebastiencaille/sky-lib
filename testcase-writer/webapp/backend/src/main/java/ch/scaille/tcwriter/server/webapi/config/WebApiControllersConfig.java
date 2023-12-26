package ch.scaille.tcwriter.server.webapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.session.SessionRepository;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import ch.scaille.tcwriter.server.facade.WebFeedbackFacade;
import ch.scaille.tcwriter.server.webapi.service.WebSocketFeedbackFacade;

@Configuration
@Import(ch.scaille.tcwriter.server.webapi.v0.WebApiControllersConfig.class)
public class WebApiControllersConfig {

	@Bean
	WebFeedbackFacade webFeedbackFacade(SessionRepository<?> sessionRepository,
			SimpMessageSendingOperations feedbackSendingTemplate) {
		return new WebSocketFeedbackFacade(sessionRepository, feedbackSendingTemplate);
	}

	@Bean
	@DependsOn("webApiServlet")
	RequestMappingHandlerMapping webApiRequestMappingHandlerMapping() {
		final var requestMappingHandlerMapping = new RequestMappingHandlerMapping();
		requestMappingHandlerMapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return requestMappingHandlerMapping;
	}
}
