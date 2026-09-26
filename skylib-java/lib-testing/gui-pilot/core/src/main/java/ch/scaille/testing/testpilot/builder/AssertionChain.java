package ch.scaille.testing.testpilot.builder;

import ch.scaille.testing.testpilot.PolledComponent;
import ch.scaille.testing.testpilot.Polling;
import ch.scaille.testing.testpilot.factories.PollingResults;
import ch.scaille.testing.testpilot.factories.Pollings;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectAssert;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class AssertionChain<C> {

		protected final AssertionBuilder<C, ?, ?, ?> builder;

		protected AssertionChain(AssertionBuilder<C, ?, ?, ?> builder) {
			this.builder = builder;
		}

		public AssertionChain<C> configure(Consumer<Polling.PollingBuilder<C, ?>> configurer) {
			builder.configurers.add(configurer);
			return this;
		}

		/**
		 * Tests if the assertion is fulfilled
		 */
		public AssertionResult<C> testAssertion(final Polling.PollingBuilder<C, Boolean> assertion) {
			return new AssertionResult<>(this, builder.poll(assertion).isSuccess());
		}

		private AssertionChain<C> withAssertj() {
			Assertions.setDescriptionConsumer(description -> builder.injectedDescriptionHolder.set(description.value()));
			return this;
		}

		public AssertionResult<C> assertThatCtxt(Consumer<ObjectAssert<PolledComponent<C>>> assertion) {
			withAssertj().appliedCtxt(c -> assertion.accept(Assertions.assertThat(c).as(c.description())));
			return new AssertionResult<>(this, true);
		}

		public AssertionResult<C> assertThat(Consumer<ObjectAssert<C>> assertion) {
		 	withAssertj().appliedCtxt(c -> assertion.accept(Assertions.assertThat(c.component()).as(c.description())));
		    return new AssertionResult<>(this, true);
		}

		public AssertionResult<C> satisfyThatCtxt(Consumer<ObjectAssert<PolledComponent<C>>> assertion) {
			return withAssertj().satisfiedCtxt(Pollings.satisfiesC(c -> assertion.accept(Assertions.assertThat(c).as(c.description()))));
		}

		public AssertionResult<C> satisfyThat(Consumer<ObjectAssert<C>> assertion) {
			return withAssertj().satisfied(Pollings.satisfiesC(c -> assertion.accept(Assertions.assertThat(c))));
		}

		public AssertionResult<C> satisfied(Predicate<C> predicate) {
			return testAssertion(Pollings.satisfies(predicate));
		}

		public AssertionResult<C> satisfiedCtxt(Predicate<PolledComponent<C>> action) {
			return testAssertion(Polling.of(ctxt -> PollingResults.value(action.test(ctxt))));
		}

		public AssertionResult<C> applied(Consumer<C> consumer) {
			return testAssertion(Pollings.applies(consumer));
		}

		public AssertionResult<C> appliedCtxt(Consumer<PolledComponent<C>> consumer) {
			return testAssertion(Pollings.appliesCtxt(consumer));
		}

		public AssertionResult<C> asserted(Consumer<C> assertion) {
			return applied(assertion);
		}

		public AssertionResult<C> assertedCtxt(Consumer<PolledComponent<C>> assertion) {
			return appliedCtxt(assertion);
		}

		/**
		 * Gets a value from the component
		 */
		public <R> Optional<R> get(Function<C, @Nullable R> getter) {
			final var pollResult = builder.poll(Pollings.get(getter));
			if (pollResult.isSuccess()) {
				return Optional.ofNullable(pollResult.polledValue());
			}
			return Optional.empty();
		}

	}
	