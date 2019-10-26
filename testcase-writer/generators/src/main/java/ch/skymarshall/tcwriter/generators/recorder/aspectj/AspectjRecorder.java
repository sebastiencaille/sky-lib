package ch.skymarshall.tcwriter.generators.recorder.aspectj;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.recorder.AbstractRecorder;

public class AspectjRecorder extends AbstractRecorder {

	public AspectjRecorder(final TestModel model) {
		super(model);
	}

	public void install() {
		TCApiAspect.setRecorder(this);
		TCRoleAspect.setRecorder(this);
	}

	public void recordActor(final ProceedingJoinPoint jp, final Object actor) {
		super.recordActor(actor);
	}

	public void recordStep(final JoinPoint jp, final Object target, final String apiName, final Object[] apiArgs) {
		recordStep(jp.toString(), target, apiName, apiArgs);
	}

	public void recordParamFactory(final JoinPoint jp, final Class<?> apiFactoryClass, final String apiName,
			final Object[] apiArgs, final Object returnValue) {
		recordParamFactory(apiFactoryClass, apiName, apiArgs, returnValue);
	}

}
