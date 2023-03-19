package ch.scaille.tcwriter.server.config;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebApplicationContext;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ch.scaille.tcwriter.server.web.controller.WebErrorController;
import ch.scaille.tcwriter.server.webapi.config.WebApiControllersConfig;
import ch.scaille.tcwriter.server.webapi.config.WebRestOpenApiDocConfig;

@Configuration
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer{

	@Bean
	ServletRegistrationBean<?> webApiServlet() {
		var annotationContext = new AnnotationConfigServletWebApplicationContext();
		annotationContext.register(WebApiControllersConfig.class, WebRestOpenApiDocConfig.class);
		annotationContext.setDisplayName("Web Api");
		var dispatcher = new DispatcherServlet(annotationContext);

		var servletRegistration = new ServletRegistrationBean<>(dispatcher, "/api/*");
		servletRegistration.setLoadOnStartup(1);
		servletRegistration.setName("Web Rest api");
		return servletRegistration;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/*").allowedMethods("*").allowedOrigins("http://localhost:3000");
	}
	
	@Bean
	MessageSource webMessages() {
		var messageSource = new ResourceBundleMessageSource();
		messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
		messageSource.setBundleClassLoader(Thread.currentThread().getContextClassLoader());
		messageSource.addBasenames("web/messages");
		return messageSource;
	}
	
	@Bean
	WebErrorController errorController(ErrorAttributes errorAttributes, ApplicationContext context, @Qualifier("webMessages") MessageSource messageSource) {
		return new WebErrorController(errorAttributes, context, messageSource);
	}

}
