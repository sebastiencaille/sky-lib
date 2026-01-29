package ch.scaille.tcwriter.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.web.filter.RequestContextFilter;

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
	SecurityFilterChain api(HttpSecurity http) {
		return http.securityMatcher("/api/*")
				.sessionManagement(c -> c.maximumSessions(2))
				.authorizeHttpRequests(a -> a.anyRequest().anonymous())
				.addFilterAfter(tomcatOverloadDetectorFilter(), BasicAuthenticationFilter.class)
				.addFilterAfter(requestContextFilter(), tomcatOverloadDetectorFilter().getClass())
				.build();
	}

	@Bean
	SecurityFilterChain webApi(HttpSecurity http) {
		return http.securityMatcher("/api/rest/*")
				.sessionManagement(c -> c.maximumSessions(2))
				.authorizeHttpRequests(a -> a.anyRequest().anonymous())
				.addFilterAfter(tomcatOverloadDetectorFilter(), BasicAuthenticationFilter.class)
				.addFilterAfter(requestContextFilter(), tomcatOverloadDetectorFilter().getClass())
				.csrf(AbstractHttpConfigurer::disable)
				.build();
	}

	@Bean
	SecurityFilterChain securityFilterChain() {
		return new DefaultSecurityFilterChain(AnyRequestMatcher.INSTANCE, tomcatOverloadDetectorFilter());
	}

	@Bean
	RequestContextFilter requestContextFilter() {
		return new RequestContextFilter();
	}

	@Bean
	TomcatOverloadDetectorFilter tomcatOverloadDetectorFilter() {
		return new TomcatOverloadDetectorFilter();
	}

}