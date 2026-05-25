package ch.scaille.tcwriter.javatc.testexec.recorder;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import ch.scaille.tcwriter.annotations.TCRole;
import org.jspecify.annotations.Nullable;

@Aspect
public class TestCaseRecorderAspect {

	@Nullable
	private static ITestCaseRecorder recorder;
	
	public static void setRecorder(@Nullable final ITestCaseRecorder recorder) {
		TestCaseRecorderAspect.recorder = recorder;
	}

	private boolean inRecord = false; 
	
	/**
	 * Configures the recording and saves the test
	 *
     */
	@Around("execution(@org.junit.jupiter.api.Test * *.*(..))")
	public Object runAroundTest(final ProceedingJoinPoint jp) throws Throwable {
		inRecord = false;
		return jp.proceed();
	}

	/**
	 * Records calls
	 *
     */
	@Around("execution(@ch.scaille.tcwriter.annotations.TCApi * *.*(..))")
	@Nullable
	public Object recordApiCall(final ProceedingJoinPoint jp) throws Throwable {
		if (recorder == null || inRecord) {
			return jp.proceed();
		}
		final var calledMethod = ((MethodSignature)jp.getSignature()).getMethod();
		if (jp.getTarget() != null) {
			inRecord = true;
			if (calledMethod.getDeclaringClass().getAnnotation(TCRole.class) != null) {
				recorder.recordStep(jp.getTarget(), calledMethod, jp.getArgs());
			} else {
				recorder.recordParamFactoryCall(jp.getTarget(), calledMethod, jp.getArgs());
			}
		}
		final var returnValue = jp.proceed();
		if (jp.getTarget() == null) {
			recorder.recordParamFactory(calledMethod.getDeclaringClass(), calledMethod, jp.getArgs(),
					returnValue);
		} else if (returnValue != null) {
			recorder.recordReturnValue(returnValue);
		}
		inRecord = false;
		return returnValue;
	}

}
