package ch.scaille.tcwriter.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.web.filter.RequestContextFilter;

import ch.scaille.tcwriter.server.dto.Context;
import ch.scaille.tcwriter.server.facade.ClusteredSessionFacade;
import ch.scaille.tcwriter.server.facade.ContextFacade;
import ch.scaille.tcwriter.server.web.filter.ClusteredSessionFilter;
import ch.scaille.tcwriter.server.web.filter.TomcatOverloadDetectorFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Bean
	InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
		final var user = User.withUsername("anon").password(passwordEncoder.encode("")).roles("anon").build();
		return new InMemoryUserDetailsManager(user);
	}

	@Bean
	PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain api(HttpSecurity http,TomcatOverloadDetectorFilter tomcatOverloadDetectorFilter, RequestContextFilter requestContextFilter,
			ClusteredSessionFilter clusteredSessionFilter) throws Exception {
		return http.securityMatcher("/api/*")
				.authorizeHttpRequests(a -> a.anyRequest().anonymous())
				.addFilterAfter(tomcatOverloadDetectorFilter, BasicAuthenticationFilter.class)
				.addFilterAfter(requestContextFilter, tomcatOverloadDetectorFilter.getClass())
				.addFilterAfter(clusteredSessionFilter, requestContextFilter.getClass())
				.build();
	}

	@Bean
	SecurityFilterChain securityFilterChain(TomcatOverloadDetectorFilter tomcatOverloadDetectorFilter) {
		return new DefaultSecurityFilterChain(AnyRequestMatcher.INSTANCE, tomcatOverloadDetectorFilter);
	}

	@Bean
	RequestContextFilter requestContextFilter() {
		return new RequestContextFilter();
	}

	@Bean
	TomcatOverloadDetectorFilter tomcatOverloadDetectorFilter() {
		return new TomcatOverloadDetectorFilter();
	}

	@Bean
	ClusteredSessionFilter clusteredSessionFilter(Context context, ClusteredSessionFacade clusteredSessionFacade,
			ContextFacade contextFacade) {
		return new ClusteredSessionFilter(context, clusteredSessionFacade, contextFacade);
	}

}