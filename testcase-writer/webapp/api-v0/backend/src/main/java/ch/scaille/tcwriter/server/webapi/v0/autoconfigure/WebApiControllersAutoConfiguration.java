package ch.scaille.tcwriter.server.webapi.v0.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.context.servlet.AnnotationConfigServletWebApplicationContext;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.DispatcherServlet;

import ch.scaille.tcwriter.server.webapi.config.JacksonConfig;
import ch.scaille.tcwriter.server.webapi.config.WebSocketConfig;

@AutoConfiguration
public class WebApiControllersAutoConfiguration {
	
	@Bean
	ServletRegistrationBean<DispatcherServlet> webApiServlet() {
		final var annotationContext = new AnnotationConfigServletWebApplicationContext();
		annotationContext.register(JacksonConfig.class, WebSocketConfig.class, 
				WebApiControllersConfig.class);
		annotationContext.setDisplayName("Web Api");
		final var dispatcher = new DispatcherServlet(annotationContext);

		final var servletRegistration = new ServletRegistrationBean<>(dispatcher, "/api/v0/*");
		servletRegistration.setLoadOnStartup(1);
		servletRegistration.setName("Web Rest api");
		return servletRegistration;
	}
	
}
