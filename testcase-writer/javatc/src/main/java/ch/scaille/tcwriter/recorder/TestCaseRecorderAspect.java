package ch.scaille.tcwriter.recorder;

import java.io.IOException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;

import ch.scaille.tcwriter.annotations.Recorded;
import ch.scaille.tcwriter.annotations.TCRole;
import ch.scaille.tcwriter.model.persistence.FsModelDao;

@Aspect
public class TestCaseRecorderAspect {

	private static ITestCaseRecorder recorder;

	public static void setRecorder(final ITestCaseRecorder recorder) {
		TestCaseRecorderAspect.recorder = recorder;
	}

	public static void configure(String fsModelConfig, String tcDictionary) throws IOException {
		final var dao = new FsModelDao(FsModelDao.loadConfiguration(fsModelConfig));
		final var recorder = new TestCaseRecorder(dao, tcDictionary);
		TestCaseRecorderAspect.setRecorder(recorder);
	}

	/**
	 * Configures the recording and saves the test
	 * 
	 * @param jp
	 * @param test
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(* *.*(..)) && @annotation(test)")
	public Object saveRecording(final ProceedingJoinPoint jp, Test test) throws Throwable {
		var signature = (MethodSignature) jp.getSignature();
		var method = signature.getMethod();

		var recorderEnabled = Boolean.getBoolean("tc.recorderEnabled");
		var fsModelConfig = System.getProperty("tc.fsModelConfig");
		var tcDictionaryName = System.getProperty("tc.dictionaryName", "default");

		var recorded = method.getAnnotation(Recorded.class);
		if (recorded != null && recorded.fsModelConfig() != null) {
			fsModelConfig = recorded.fsModelConfig();
		}
		if (recorded != null && recorded.dictionary() != null) {
			tcDictionaryName = recorded.dictionary();
		}
		if (recorder == null && (recorderEnabled || (recorded != null && recorded.enabled()))) {
			setRecorder(new TestCaseRecorder(new FsModelDao(FsModelDao.loadConfiguration(fsModelConfig)),
					tcDictionaryName));
		}
		var result = jp.proceed();
		if (recorder != null) {
			recorder.save(jp.getSignature().getDeclaringTypeName() + '-' + jp.getSignature().getName());
		}
		return result;
	}

	/**
	 * Records calls
	 * 
	 * @param jp
	 * @return
	 * @throws Throwable
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
