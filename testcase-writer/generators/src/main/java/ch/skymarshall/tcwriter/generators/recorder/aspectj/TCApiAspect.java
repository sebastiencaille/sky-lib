package ch.skymarshall.tcwriter.generators.recorder.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class TCApiAspect {

	private static AspectjRecorder recorder;

	public static void setRecorder(final AspectjRecorder recorder) {
		TCApiAspect.recorder = recorder;
	}

	@Around("execution(@ch.skymarshall.tcwriter.annotations.TCApi * *.*(..))")
	public Object recordApiCall(final ProceedingJoinPoint jp) throws Throwable {
		if (recorder == null) {
			throw new IllegalStateException("recorder not set");
		}
		if (jp.getTarget() != null) {
			recorder.recordStep(jp, jp.getTarget(), jp.getSignature().getName(), jp.getArgs());
		}
		final Object returnValue = jp.proceed();
		if (jp.getTarget() == null) {
			recorder.recordParamFactory(jp, jp.getSignature().getDeclaringType(), jp.getSignature().getName(),
					jp.getArgs(), returnValue);
		}
		return returnValue;
	}

}