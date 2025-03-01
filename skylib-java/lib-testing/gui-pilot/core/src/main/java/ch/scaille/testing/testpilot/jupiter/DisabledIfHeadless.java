package ch.scaille.testing.testpilot.jupiter;

import java.awt.GraphicsEnvironment;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class DisabledIfHeadless implements ExecutionCondition {

	@Override
	public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
		if (GraphicsEnvironment.isHeadless()) {
			return ConditionEvaluationResult.disabled("Headless mode is active");
		}
		return ConditionEvaluationResult.enabled("Headless mode is not active");
	}
}
