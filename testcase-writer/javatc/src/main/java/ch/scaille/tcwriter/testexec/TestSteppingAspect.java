package ch.scaille.tcwriter.testexec;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;

import ch.scaille.tcwriter.annotations.TCRole;

@Aspect
public class TestSteppingAspect {

	private static ITestExecutionFeedbackClient feedbackClient;

	public static void setFeedbackClient(final ITestExecutionFeedbackClient feedbackClient) {
		TestSteppingAspect.feedbackClient = feedbackClient;
	}

	@Around("execution(@ch.scaille.tcwriter.annotations.TCApi * *.*(..))")
	public Object apiCallControl(final ProceedingJoinPoint jp) throws Throwable {
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

	@Around("execution(* *.*(..)) && @annotation(test)")
	public Object testControl(final ProceedingJoinPoint jp, final Test test) throws Throwable {
		if (!Boolean.getBoolean("tc.stepping")) {
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

}
