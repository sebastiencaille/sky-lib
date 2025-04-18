package ch.scaille.tcwriter.server.config;

import java.nio.charset.StandardCharsets;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ch.scaille.tcwriter.server.webapi.config.JacksonConfig;
import ch.scaille.tcwriter.server.webapi.config.WebApiControllersConfig;
import ch.scaille.tcwriter.server.webapi.config.WebSocketConfig;

@Configuration
@Import({ch.scaille.tcwriter.generated.api.config.v0.SpringDocConfiguration.class})
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

	private static final String ALLOWED_ORIGIN = "http://localhost:9000";
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("/static/");
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/*").allowedMethods("*").allowedOrigins(ALLOWED_ORIGIN);
	}

	@Bean
	MessageSource webMessages() {
		final var messageSource = new ResourceBundleMessageSource();
		messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
		messageSource.setBundleClassLoader(Thread.currentThread().getContextClassLoader());
		messageSource.addBasenames("web/messages");
		return messageSource;
	}

	@Bean
	ServletRegistrationBean<?> webApiServlet() {
		final var annotationContext = new AnnotationConfigServletWebApplicationContext();
		annotationContext.register(JacksonConfig.class, WebSocketConfig.class, WebApiControllersConfig.class);
		annotationContext.setDisplayName("Web Api");
		final var dispatcher = new DispatcherServlet(annotationContext);

		final var servletRegistration = new ServletRegistrationBean<>(dispatcher, "/api/*");
		servletRegistration.setLoadOnStartup(1);
		servletRegistration.setName("Web Rest api");
		return servletRegistration;
	}

}
