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

	private boolean inRecord = false; 
	
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
		inRecord = false;
		return jp.proceed();
	}

	/**
	 * Records calls
	 *
     */
	@Around("execution(@ch.scaille.tcwriter.annotations.TCApi * *.*(..))")
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
