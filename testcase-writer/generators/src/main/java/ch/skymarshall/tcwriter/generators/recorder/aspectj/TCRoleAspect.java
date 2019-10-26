package ch.skymarshall.tcwriter.generators.recorder.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class TCRoleAspect {

	private static AspectjRecorder recorder;

	public static void setRecorder(final AspectjRecorder recorder) {
		TCRoleAspect.recorder = recorder;
	}

	@Around("within(@ch.skymarshall.tcwriter.annotations.TCRole *) && execution(*.new(..))")
	public Object recordApiCall(final ProceedingJoinPoint jp) throws Throwable {
		if (recorder == null) {
			throw new IllegalStateException("recorder not set");
		}
		final Object returnValue = jp.proceed();
		if (jp.getTarget() != null) {
			// recorder.recordActor(jp, returnValue);
		}
		return returnValue;
	}

}
