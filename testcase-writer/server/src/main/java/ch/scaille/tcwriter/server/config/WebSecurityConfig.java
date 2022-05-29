package ch.scaille.tcwriter.server.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import ch.scaille.tcwriter.server.services.ContextService;
import ch.scaille.tcwriter.server.web.filters.ContextFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public FilterRegistrationBean<ContextFilter> contextFilter(ContextService contextService) {
		FilterRegistrationBean<ContextFilter> registrationBean = new FilterRegistrationBean<>();

		registrationBean.setFilter(new ContextFilter(contextService));
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(2);

		return registrationBean;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.antMatcher("/*").anonymous();
	}

}