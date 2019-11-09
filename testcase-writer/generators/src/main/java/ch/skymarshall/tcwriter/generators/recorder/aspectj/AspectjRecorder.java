package ch.skymarshall.tcwriter.generators.recorder.aspectj;

import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.recorder.AbstractRecorder;

public class AspectjRecorder extends AbstractRecorder {

	public AspectjRecorder(final TestModel model) {
		super(model);
	}

	public void install() {
		TCApiAspect.setRecorder(this);
	}

}
