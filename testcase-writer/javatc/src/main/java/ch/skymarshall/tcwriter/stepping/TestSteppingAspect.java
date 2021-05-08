package ch.skymarshall.tcwriter.stepping;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;

import ch.skymarshall.tcwriter.annotations.TCRole;

@Aspect
public class TestSteppingAspect {

	private static ITestSteppingController controller;

	public static void setController(final ITestSteppingController controller) {
		TestSteppingAspect.controller = controller;
	}

	@Around("execution(@ch.skymarshall.tcwriter.annotations.TCApi * *.*(..))")
	public Object apiCallControl(final ProceedingJoinPoint jp) throws Throwable {
		if (controller == null) {
			return jp.proceed();
		}
		final boolean roleCall = jp.getTarget() != null
				&& jp.getSignature().getDeclaringType().isAnnotationPresent(TCRole.class);
		if (roleCall) {
			controller.beforeStepExecution();
		}
		final Object returnValue = jp.proceed();
		if (roleCall) {
			controller.afterStepExecution();
		}
		return returnValue;
	}

	@Around("execution(* *.*(..)) && @annotation(test)")
	public Object testControl(final ProceedingJoinPoint jp, final Test test) throws Throwable {
		if (controller == null) {
			return jp.proceed();
		}
		controller.beforeTestExecution();
		try {
			return jp.proceed();
		} catch (final Throwable t) {
			controller.notifyError(t);
			throw t;
		}
	}
}
