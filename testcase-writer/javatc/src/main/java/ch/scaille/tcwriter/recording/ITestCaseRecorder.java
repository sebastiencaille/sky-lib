package ch.scaille.tcwriter.recording;

import java.io.IOException;
import java.nio.file.Path;

public interface ITestCaseRecorder {

	void recordStep(String description, Object recordedActor, String apiName, Object[] apiArgs);

	void recordParamFactory(Class<?> apiFactoryClass, String apiName, Object[] apiArgs, Object returnValue);

	void recordParamFactoryCall(Object factory, String callName, Object[] args);

	void recordReturnValue(Object reference);

	void save(Path testRoot, String testName) throws IOException;
}
