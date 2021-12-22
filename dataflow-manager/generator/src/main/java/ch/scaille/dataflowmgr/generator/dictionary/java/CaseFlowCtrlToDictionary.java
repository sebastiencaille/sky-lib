package ch.scaille.dataflowmgr.generator.dictionary.java;

import java.lang.reflect.Method;
import java.security.InvalidParameterException;

import ch.scaille.dataflowmgr.annotations.Conditions;
import ch.scaille.dataflowmgr.model.CustomCall;
import ch.scaille.dataflowmgr.model.Dictionary;
import ch.scaille.dataflowmgr.model.Dictionary.Calls;

public class CaseFlowCtrlToDictionary extends AbstractClassToDictionary {

	Calls<CustomCall> calls = new Calls<>("condition", CustomCall::derivate);

	public void addToDictionary(Dictionary dictionary, Class<?> clazz) {
		if (clazz.isAnnotationPresent(Conditions.class)) {
			methodsOf(clazz).forEach(m -> calls.add(conditionFrom(m)));
			dictionary.flowControl.put(Conditions.class, calls);
		}
	}

	private CustomCall conditionFrom(final Method m) {
		if (!Boolean.TYPE.equals(m.getReturnType())) {
			throw new InvalidParameterException("Condition method must return a boolean:" + m);
		}
		return new CustomCall(methodFullName(m), m.getName(), parameters(m), Boolean.TYPE.getName());
	}

}
