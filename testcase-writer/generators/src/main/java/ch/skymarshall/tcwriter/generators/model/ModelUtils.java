package ch.skymarshall.tcwriter.generators.model;

import ch.skymarshall.tcwriter.generators.model.testapi.TestAction;
import ch.skymarshall.tcwriter.generators.model.testapi.TestApiParameter;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;

public interface ModelUtils {

	public static class ActionUtils {
		private final TestModel tm;
		private final TestAction testAction;

		public ActionUtils(final TestModel tm, final TestAction testAction) {
			this.tm = tm;
			this.testAction = testAction;
		}

		public boolean hasSelector() {
			return !testAction.getParameters().isEmpty() && tm.isSelector(testAction.getParameter(0));
		}

		public boolean hasActionParameter(final int index) {
			return actionParameterIndex(index) < testAction.getParameters().size();
		}

		public int selectorIndex() {
			if (!hasSelector()) {
				throw new IllegalStateException("No selector found: " + testAction);
			}
			return 0;
		}

		public int actionParameterIndex(final int index) {
			int paramStartIndex;
			if (hasSelector()) {
				paramStartIndex = selectorIndex() + 1;
			} else {
				paramStartIndex = 0;
			}
			return paramStartIndex + index;
		}

		public TestApiParameter selectorType() {
			return testAction.getParameter(selectorIndex());
		}

		public TestApiParameter parameterType(final int index) {
			return testAction.getParameter(actionParameterIndex(index));
		}

	}

	public static ActionUtils actionUtils(final TestModel tm, final TestAction testAction) {
		return new ActionUtils(tm, testAction);
	}

}
