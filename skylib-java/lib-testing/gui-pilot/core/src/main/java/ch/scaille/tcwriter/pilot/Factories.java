package ch.scaille.tcwriter.pilot;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import ch.scaille.tcwriter.pilot.PollingResult.FailureHandler;
import ch.scaille.util.dao.metadata.DataObjectManagerFactory;

public interface Factories {

	interface Pollings {

		/*************************** Pollings ***************************/

		/**
		 * Make polling successful if condition is accepted
		 *
		 * @param <C>
		 * @param <V>
		 * @param reason
		 * @return
		 */
		static <C> Polling<C, Boolean> satisfies(final Predicate<C> predicate) {
			return new Polling<>(null, c -> {
				if (!predicate.test(c.component)) {
					return PollingResults.failure("Condition not met");
				}
				return PollingResults.success();
			});
		}

		/**
		 * Make polling successful if assert is successful
		 *
		 * @param <C>
		 * @param <V>
		 * @param reason
		 * @return
		 */
		static <C> Polling<C, Boolean> assertion(final Consumer<PollingContext<C>> assertion) {
			return new Polling<>(null, c -> {
				try {
					assertion.accept(c);
					return PollingResults.success();
				} catch (final AssertionError e) {
					return PollingResults.onException(e);
				}
			});
		}

		/**
		 * Make polling successful after action is applied
		 *
		 * @param <C>
		 * @param action
		 * @return
		 */
		static <C> Polling<C, Boolean> action(final Consumer<C> action) {
			return new ActionPolling<>(null, c -> {
				action.accept(c.component);
				return PollingResults.success();
			});
		}

		static <C> Polling<C, Boolean> success(final Consumer<C> action) {
			return new Polling<>(null, c -> {
				action.accept(c.component);
				return PollingResults.success();
			});
		}
	}

	/*************************** Polling Results ***************************/
    interface PollingResults {
		/**
		 * Make polling return a value
		 *
		 * @param <C>
		 * @param <V>
		 * @param value
		 * @return
		 */
		static <C, V> PollingResult<C, V> value(final V value) {
			return new PollingResult<>(value, null);
		}

		/**
		 * Make polling return value "true"
		 *
		 * @param <C>
		 * @return
		 */
		static <C> PollingResult<C, Boolean> success() {
			return new PollingResult<>(Boolean.TRUE, null);
		}

		/**
		 * Make polling return value "false"
		 *
		 * @param <C>
		 * @return
		 */
		static <C> PollingResult<C, Boolean> failed() {
			return new PollingResult<>(Boolean.FALSE, null);
		}

		/**
		 * Make polling return a failure
		 *
		 * @param <C>
		 * @return
		 */
		static <C, V> PollingResult<C, V> failure(final String reason) {
			return new PollingResult<>(null, new AssertionError(reason));
		}

		/**
		 * Make polling return a failure
		 *
		 * @param <C>
		 * @return
		 */
		static <C, V> PollingResult<C, V> onException(final Throwable cause) {
			return new PollingResult<>(null, cause);
		}

	}

	interface FailureHandlers {
		/*************************** Failure handlers ***************************/

		/**
		 * Fails using some text
		 *
		 * @param actionDescr
		 * @return
		 */
		static <C, V> FailureHandler<C, V> throwError(final String actionDescr) {
			return (r, g) -> {
				throw new AssertionError(
						r.getComponentDescription() + ": action failed [" + actionDescr + "]: " + r.failureReason);
			};
		}

		/**
		 * Throws an AssertionError
		 *
		 * @return
		 */
		static <C, V> FailureHandler<C, V> throwError() {
			return (r, g) -> {
				if (r.failureReason instanceof AssertionError) {
					throw new AssertionError(r.getComponentDescription() + ": " + r.failureReason.getMessage(),
							r.failureReason.getCause());
				}
				throw new AssertionError(r.getComponentDescription() + ": " + r.failureReason.getMessage(),
						r.failureReason);
			};
		}

		/**
		 * Only reports error and return a null value
		 *
		 * @param actionDescr
		 * @return
		 */
		static <C, V> FailureHandler<C, V> reportFailure(final String report) {
			return (r, g) -> {
				g.getActionReport().report(report);
				return r.polledValue;
			};
		}

		/**
		 * Do nothing on error and return a null value
		 *
		 * @param actionDescr
		 * @return
		 */
		static <C, V> FailureHandler<C, V> ignoreFailure() {
			return (r, g) -> r.polledValue;
		}

		/**
		 * Only reports error and return FALSE
		 *
		 * @param actionDescr
		 * @return
		 */
		static <C> FailureHandler<C, Boolean> reportNotFound(final String report) {
			return (r, g) -> {
				g.getActionReport().report(report);
				return Boolean.FALSE;
			};
		}

		/**
		 * Do nothing on error and return FALSE
		 *
		 * @param actionDescr
		 * @return
		 */
		static <C> FailureHandler<C, Boolean> ignoreNotFound() {
			return (r, g) -> Boolean.FALSE;
		}
	}

	/**
	 * No precondition tested
	 *
	 * @return a precondition that is always true
	 */
	static <C> Predicate<C> none() {
		return p -> true;
	}

	// Report factories
    interface Reporting {

		static String settingValue(String value) {
			return "setting: " + value;
		}

		static String settingValue(String location, String value) {
			return "setting " + location + ": " + value;
		}

		static String checkingThat(String message) {
			return "checking that " + message;
		}

		static String checkingValue(String value) {
			return "checking value: " + value;
		}

		static String checkingValue(String location, String value) {
			return "checking value " + location + ": " + value;
		}

		static String settingValue(String location, Object value) {
			return "setting " + location + ": ["
					+ DataObjectManagerFactory.createFor(value).getMetaData().getAttributes().stream() //
							.filter(a -> a.getValueOf(value) != null) //
							.sorted((a1, a2) -> a1.getName().compareTo(a2.getName())) //
							.map(a -> a.getName() + ": " + a.getValueOf(value)) //
							.collect(Collectors.joining(", "))
					+ "]";
		}
	}
}
