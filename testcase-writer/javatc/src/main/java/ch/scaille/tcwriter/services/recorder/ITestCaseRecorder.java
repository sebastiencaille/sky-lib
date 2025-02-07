package ch.scaille.tcwriter.services.recorder;

import java.lang.reflect.Method;

import ch.scaille.tcwriter.model.testcase.TestCase;

public interface ITestCaseRecorder {

	void recordStep(Object recordedActor, Method api, Object[] apiArgs);

	void recordParamFactory(Class<?> apiFactoryClass, Method api, Object[] apiArgs, Object returnValue);

	void recordParamFactoryCall(Object factory, Method api, Object[] args);

	void recordReturnValue(Object reference);

	TestCase buildTestCase(String testName);
}
