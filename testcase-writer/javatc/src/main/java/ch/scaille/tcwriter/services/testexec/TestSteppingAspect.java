package ch.scaille.tcwriter.services.testexec;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import ch.scaille.tcwriter.annotations.TCRole;
import ch.scaille.util.helpers.Logs;

@Aspect
public class TestSteppingAspect {

	private static ITestExecutionFeedbackClient feedbackClient;

	public static void setFeedbackClient(final ITestExecutionFeedbackClient feedbackClient) {
		TestSteppingAspect.feedbackClient = feedbackClient;
	}
	
	@Around("execution(@org.junit.jupiter.api.Test * *.*(..))")
	public Object testControl(final ProceedingJoinPoint jp) throws Throwable {
		final var steppingEnabled = Boolean.getBoolean("tc.stepping");
		Logs.of(TestSteppingAspect.class).info(() -> "Stepping enabled: " + steppingEnabled);
		if (!steppingEnabled) {
			return jp.proceed();
		}
		setFeedbackClient(new TestExecutionFeedbackClient());
		feedbackClient.beforeTestExecution();
		try {
			return jp.proceed();
		} catch (final Throwable t) {
			feedbackClient.notifyError(t);
			throw t;
		}
	}

	@Around("execution(@ch.scaille.tcwriter.annotations.TCApi * *.*(..))")
	public Object apiCallControl(final ProceedingJoinPoint jp) throws Throwable {
		Logs.of(TestSteppingAspect.class).info(() -> "Stepping in " + jp.getSignature().getName());
		if (feedbackClient == null) {
			return jp.proceed();
		}
		final var roleCall = jp.getTarget() != null
				&& jp.getSignature().getDeclaringType().isAnnotationPresent(TCRole.class);
		if (roleCall) {
			feedbackClient.beforeStepExecution();
		}
		final var returnValue = jp.proceed();
		if (roleCall) {
			feedbackClient.afterStepExecution();
		}
		return returnValue;
	}

}
