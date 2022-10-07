package ch.scaille.tcwriter.server.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ch.scaille.tcwriter.server.webapi.config.WebRestControllersConfig;
import ch.scaille.tcwriter.server.webapi.config.WebRestOpenApiDocConfig;

@Configuration
public class WebConfig {

	@Bean
	public ServletRegistrationBean<?> webApiServlet() {
		var annotationContext = new AnnotationConfigServletWebApplicationContext();
		annotationContext.register(WebRestControllersConfig.class);
		annotationContext.register(WebRestOpenApiDocConfig.class);
		annotationContext.setDisplayName("Web Api");
		var dispatcher = new DispatcherServlet(annotationContext);

		var servletRegistration = new ServletRegistrationBean<>(dispatcher, "/api/*");
		servletRegistration.setLoadOnStartup(1);
		servletRegistration.setName("Web Rest api");
		return servletRegistration;
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/*").allowedMethods("*").allowedOrigins("http://localhost:3000");
			}
		};
	}

}
