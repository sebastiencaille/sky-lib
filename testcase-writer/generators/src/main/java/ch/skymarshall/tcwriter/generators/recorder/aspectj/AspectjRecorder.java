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

	public void recordStep(final JoinPoint jp, final Object actor, final String apiName, final Object[] apiArgs) {
		recordStep(jp.toString(), actor, apiName, apiArgs);
	}

	public void recordParamFactory(final JoinPoint jp, final Class<?> apiFactoryClass, final String apiName,
			final Object[] apiArgs, final Object returnValue) {
		recordParamFactory(apiFactoryClass, apiName, apiArgs, returnValue);
	}

	public void recordParamFactoryCall(final ProceedingJoinPoint jp, final Object factory, final String callName,
			final Object[] args) {
		recordParamFactoryCall(jp.toString(), factory, callName, args);
	}

}
