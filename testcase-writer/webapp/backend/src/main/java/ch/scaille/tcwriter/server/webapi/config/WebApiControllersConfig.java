package ch.scaille.tcwriter.server.webapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import ch.scaille.tcwriter.server.facade.WebFeedbackFacade;
import ch.scaille.tcwriter.server.webapi.service.WebSocketFeedbackFacade;

/**
 * Re-enable transactions because we are in another context, and use proxyTargetClass to allow Controllers detection
 */
@Configuration
@Import(ch.scaille.tcwriter.server.webapi.v0.WebApiControllersConfig.class)
@EnableTransactionManagement(proxyTargetClass = true)
public class WebApiControllersConfig {

	@Bean
	WebFeedbackFacade webFeedbackFacade(SimpMessageSendingOperations feedbackSendingTemplate) {
		return new WebSocketFeedbackFacade(feedbackSendingTemplate);
	}

	@Bean
	@DependsOn("webApiServlet")
	RequestMappingHandlerMapping webApiRequestMappingHandlerMapping() {
		final var requestMappingHandlerMapping = new RequestMappingHandlerMapping();
		requestMappingHandlerMapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return requestMappingHandlerMapping;
	}
}
