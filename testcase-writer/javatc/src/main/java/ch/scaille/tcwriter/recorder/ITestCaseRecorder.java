package ch.scaille.tcwriter.recorder;

import java.io.IOException;
import java.nio.file.Path;

import ch.scaille.tcwriter.model.testcase.TestCase;

public interface ITestCaseRecorder {

	void recordStep(String description, Object recordedActor, String apiName, Object[] apiArgs);

	void recordParamFactory(Class<?> apiFactoryClass, String apiName, Object[] apiArgs, Object returnValue);

	void recordParamFactoryCall(Object factory, String callName, Object[] args);

	void recordReturnValue(Object reference);
	
	void save(String testName) throws IOException;

	TestCase getTestCase(String testName);
}
