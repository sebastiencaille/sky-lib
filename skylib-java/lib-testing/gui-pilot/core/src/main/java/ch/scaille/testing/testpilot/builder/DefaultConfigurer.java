package ch.scaille.testing.testpilot.builder;

public class DefaultConfigurer<C> extends Configurer<C, DefaultConfigurer<C>> {
		public DefaultConfigurer(AssertionBuilder<C, ?, ?, ?> assertionBuilder) {
			super(assertionBuilder);
		}
	}
