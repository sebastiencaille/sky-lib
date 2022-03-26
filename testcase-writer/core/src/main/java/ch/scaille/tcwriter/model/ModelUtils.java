package ch.scaille.tcwriter.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ch.scaille.tcwriter.model.testapi.TestAction;
import ch.scaille.tcwriter.model.testapi.TestApiParameter;
import ch.scaille.tcwriter.model.testapi.TestDictionary;
import ch.scaille.tcwriter.model.testapi.TestParameterFactory;
import ch.scaille.tcwriter.model.testcase.TestParameterValue;
import ch.scaille.tcwriter.model.testcase.TestStep;

public interface ModelUtils {

	public static class ActionUtils {
		private final TestDictionary tm;
		private final TestAction testAction;

		public ActionUtils(final TestDictionary tm, final TestAction testAction) {
			this.tm = tm;
			this.testAction = testAction;
		}

		public boolean hasSelector() {
			return !testAction.getParameters().isEmpty() && tm.isSelector(testAction.getParameter(0));
		}

		public boolean hasActionParameter(final int index) {
			return parameterIndex(index) < testAction.getParameters().size();
		}

		public int selectorIndex() {
			if (!hasSelector()) {
				throw new IllegalStateException("No selector found: " + testAction);
			}
			return 0;
		}

		public int parameterIndex(final int index) {
			int paramStartIndex;
			if (hasSelector()) {
				paramStartIndex = selectorIndex() + 1;
			} else {
				paramStartIndex = 0;
			}
			return paramStartIndex + index;
		}

		public TestApiParameter selector() {
			return testAction.getParameter(selectorIndex());
		}

		public TestApiParameter parameter(final int index) {
			return testAction.getParameter(parameterIndex(index));
		}

		/**
		 * In case of mismatch, when a new action is selected
		 */
		public void synchronizeStep(final TestStep step) {
			final List<TestParameterValue> parametersValue = step.getParametersValue();
			final List<TestParameterValue> newParametersValues = new ArrayList<>();

			if (hasSelector()) {
				final TestApiParameter selector = selector();
				final Optional<TestParameterValue> selectorMatch = parametersValue.stream()
						.filter(p -> p.matches(selector)).findFirst();
				if (selectorMatch.isPresent()) {
					newParametersValues.add(selectorMatch.get());
				} else {
					newParametersValues.add(new TestParameterValue(selector, TestParameterFactory.unSet(selector)));
				}

			}

			if (hasActionParameter(0)) {
				final TestApiParameter parameter = parameter(0);
				final Optional<TestParameterValue> valueMatch = parametersValue.stream()
						.filter(p -> p.matches(parameter)).findFirst();
				if (valueMatch.isPresent()) {
					newParametersValues.add(valueMatch.get());
				} else {
					newParametersValues.add(new TestParameterValue(parameter, TestParameterFactory.unSet(parameter)));
				}
			}
			step.getParametersValue().clear();
			step.getParametersValue().addAll(newParametersValues);
		}

	}

	public static ActionUtils actionUtils(final TestDictionary tm, final TestAction testAction) {
		return new ActionUtils(tm, testAction);
	}

}
