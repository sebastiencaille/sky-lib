package ch.scaille.tcwriter.server.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ch.scaille.tcwriter.server.webapi.config.WebRestControllersConfig;

@Configuration
public class WebConfig {

	@Bean
	public ServletRegistrationBean<?> webApiServlet() {
		AnnotationConfigServletWebApplicationContext ctx = new AnnotationConfigServletWebApplicationContext();
		ctx.register(WebRestControllersConfig.class);
		ctx.setDisplayName("Web Api");
		DispatcherServlet ds = new DispatcherServlet(ctx);

		ServletRegistrationBean<?> registration = new ServletRegistrationBean<>(ds, "/api/*");
		registration.setLoadOnStartup(1);
		registration.setName("Web Rest api");
		return registration;
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
