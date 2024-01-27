package ch.scaille.tcwriter.services.recorder;

import ch.scaille.tcwriter.model.testcase.TestCase;

public interface ITestCaseRecorder {

	void recordStep(String description, Object recordedActor, String apiName, Object[] apiArgs);

	void recordParamFactory(Class<?> apiFactoryClass, String apiName, Object[] apiArgs, Object returnValue);

	void recordParamFactoryCall(Object factory, String callName, Object[] args);

	void recordReturnValue(Object reference);

	TestCase buildTestCase(String testName);
}
