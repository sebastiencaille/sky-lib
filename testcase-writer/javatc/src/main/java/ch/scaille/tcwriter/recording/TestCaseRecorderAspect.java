package ch.scaille.tcwriter.recording;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import ch.scaille.tcwriter.annotations.TCRole;

@Aspect
public class TestCaseRecorderAspect {

	private static ITestCaseRecorder recorder;

	public static void setRecorder(final ITestCaseRecorder recorder) {
		TestCaseRecorderAspect.recorder = recorder;
	}

	@Around("execution(@ch.scaille.tcwriter.annotations.TCApi * *.*(..))")
	public Object recordApiCall(final ProceedingJoinPoint jp) throws Throwable {
		if (recorder == null) {
			return jp.proceed();
		}
		if (jp.getTarget() != null) {
			if (jp.getSignature().getDeclaringType().getAnnotation(TCRole.class) != null) {
				recorder.recordStep(jp.toString(), jp.getTarget(), jp.getSignature().getName(), jp.getArgs());
			} else {
				recorder.recordParamFactoryCall(jp.getTarget(), jp.getSignature().getName(), jp.getArgs());
			}
		}
		final Object returnValue = jp.proceed();
		if (jp.getTarget() == null) {
			recorder.recordParamFactory(jp.getSignature().getDeclaringType(), jp.getSignature().getName(), jp.getArgs(),
					returnValue);
		} else if (returnValue != null) {
			recorder.recordReturnValue(returnValue);
		}
		return returnValue;
	}

}
