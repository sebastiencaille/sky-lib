package ch.scaille.tcwriter.services.generators;

import java.lang.reflect.Method;

import ch.scaille.tcwriter.model.testcase.TestStep;

public class Helper {

	private Helper() {
	}

	public static String paramKey(final Method apiMethod, final int i) {
		return "param-" + apiMethod.getDeclaringClass().getName() + "." + apiMethod.getName() + "-" + i;
	}

	public static String roleKey(final Class<?> clazz) {
		return "role-" + clazz.getName();
	}

	public static String methodKey(final Method method) {
		return "method-" + method.getDeclaringClass().getName() + "." + method.getName();
	}

	public static String methodKey(final Class<?> declaringClass, final String methodName) {
		return "method-" + declaringClass.getName() + "." + methodName;
	}

	public static String valueId(final TestStep step, final int index) {
		return "step_" + step.getOrdinal() + "-val_" + index;
	}

}
