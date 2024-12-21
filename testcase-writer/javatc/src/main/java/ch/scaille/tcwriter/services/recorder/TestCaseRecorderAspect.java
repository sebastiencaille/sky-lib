package ch.scaille.tcwriter.services.recorder;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import ch.scaille.tcwriter.annotations.Recorded;
import ch.scaille.tcwriter.annotations.TCRole;
import ch.scaille.tcwriter.persistence.factory.DaoConfigs;

@Aspect
public class TestCaseRecorderAspect {

	private static ITestCaseRecorder recorder;

	public static void setRecorder(final ITestCaseRecorder recorder) {
		TestCaseRecorderAspect.recorder = recorder;
	}

	/**
	 * Configures the recording and saves the test
	 *
     */
	@Around("execution(@org.junit.jupiter.api.Test * *.*(..))")
	public Object runAroundTest(final ProceedingJoinPoint jp) throws Throwable {
		final var signature = (MethodSignature) jp.getSignature();
		final var method = signature.getMethod();

		// execution configuration
		final var recorderEnabled = Boolean.getBoolean("tc.recorderEnabled");
		var tcDictionaryName = System.getProperty("tc.dictionaryName", "default");

		final var recorded = method.getAnnotation(Recorded.class);
		if (recorded != null && recorded.dictionary() != null) {
			tcDictionaryName = recorded.dictionary();
		}
		if (recorder == null && (recorderEnabled || (recorded != null && recorded.enabled()))) {
			// When running in separate process, dynamically create a recorder 
			final var daoConfig = DaoConfigs.withFolder(DaoConfigs.tempFolder());
			setRecorder(new TestCaseRecorder(daoConfig.modelDao(), tcDictionaryName));
		}
		return jp.proceed();
	}

	/**
	 * Records calls
	 *
     */
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
		final var returnValue = jp.proceed();
		if (jp.getTarget() == null) {
			recorder.recordParamFactory(jp.getSignature().getDeclaringType(), jp.getSignature().getName(), jp.getArgs(),
					returnValue);
		} else if (returnValue != null) {
			recorder.recordReturnValue(returnValue);
		}
		return returnValue;
	}

}
