package ch.scaille.tcwriter.model;

import java.util.ArrayList;

import ch.scaille.tcwriter.model.dictionary.TestAction;
import ch.scaille.tcwriter.model.dictionary.TestApiParameter;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.dictionary.TestParameterFactory;
import ch.scaille.tcwriter.model.testcase.ExportableTestParameterValue;
import ch.scaille.tcwriter.model.testcase.TestParameterValue;
import ch.scaille.tcwriter.model.testcase.TestStep;

public interface ModelUtils {

	class ActionUtils {
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
			final var parametersValue = step.getParametersValue();
			final var newParametersValues = new ArrayList<TestParameterValue>();

			if (hasSelector()) {
				final var selector = selector();
				final var selectorMatch = parametersValue.stream()
						.filter(p -> p.matches(selector)).findFirst();
				if (selectorMatch.isPresent()) {
					newParametersValues.add(selectorMatch.get());
				} else {
					newParametersValues.add(new ExportableTestParameterValue(selector, TestParameterFactory.unSet(selector)));
				}

			}

			if (hasActionParameter(0)) {
				final var parameter = parameter(0);
				final var valueMatch = parametersValue.stream()
						.filter(p -> p.matches(parameter)).findFirst();
				if (valueMatch.isPresent()) {
					newParametersValues.add(valueMatch.get());
				} else {
					newParametersValues.add(new ExportableTestParameterValue(parameter, TestParameterFactory.unSet(parameter)));
				}
			}
			step.getParametersValue().clear();
			step.getParametersValue().addAll(newParametersValues);
		}

	}

	static ActionUtils actionUtils(final TestDictionary tm, final TestAction testAction) {
		return new ActionUtils(tm, testAction);
	}

}
