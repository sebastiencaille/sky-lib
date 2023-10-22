package ch.scaille.tcwriter.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

import ch.scaille.tcwriter.server.web.filters.TomcatOverloadDetectorFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Bean
	InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
		final var user = User.withUsername("admin").password(passwordEncoder.encode("password")).roles("ADMIN").build();
		return new InMemoryUserDetailsManager(user);
	}

	@Bean
	PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain api(HttpSecurity http) throws Exception {
		return http.securityMatcher("/api/*").
				authorizeHttpRequests(a -> a.dispatcherTypeMatchers(HttpMethod.PUT).anonymous().anyRequest().anonymous()).build();
	}

	@Bean
	SecurityFilterChain securityFilterChain(TomcatOverloadDetectorFilter tomcatOverloadDetectorFilter) {
		return new DefaultSecurityFilterChain(AnyRequestMatcher.INSTANCE, tomcatOverloadDetectorFilter);
	}

	@Bean
	TomcatOverloadDetectorFilter tomcatOverloadDetectorFilter() {
		return new TomcatOverloadDetectorFilter();
	}

}