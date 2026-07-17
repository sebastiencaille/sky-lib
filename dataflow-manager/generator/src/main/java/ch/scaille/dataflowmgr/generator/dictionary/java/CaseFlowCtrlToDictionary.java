package ch.scaille.dataflowmgr.generator.dictionary.java;

import java.lang.reflect.Method;
import java.security.InvalidParameterException;

import ch.scaille.dataflowmgr.annotations.Conditions;
import ch.scaille.dataflowmgr.model.GenericCall;
import ch.scaille.dataflowmgr.model.Dictionary;
import ch.scaille.dataflowmgr.model.Dictionary.Calls;

public class CaseFlowCtrlToDictionary extends AbstractClassToDictionary {

	final Calls<GenericCall> calls = new Calls<>("condition", GenericCall::derivate);

	public void addToDictionary(Dictionary dictionary, Class<?> clazz) {
		if (clazz.isAnnotationPresent(Conditions.class)) {
			methodsOf(clazz).forEach(m -> calls.add(conditionFrom(m)));
			dictionary.flowControl.put(Conditions.class, calls);
		}
	}

	private GenericCall conditionFrom(final Method m) {
		if (!Boolean.TYPE.equals(m.getReturnType())) {
			throw new InvalidParameterException("Condition method must return a boolean:" + m);
		}
		return new GenericCall(methodFullName(m), m.getName(), parameters(m), Boolean.TYPE.getName());
	}

}
