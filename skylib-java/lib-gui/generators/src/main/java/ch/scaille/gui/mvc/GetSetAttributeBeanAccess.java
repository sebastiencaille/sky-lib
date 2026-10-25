package ch.scaille.gui.mvc;

import ch.scaille.gui.mvc.AttributeProcessor.PrimitiveProcessor;

public class GetSetAttributeBeanAccess implements BeanAccess {
	@Override
	public String getFieldCreation(final AttributeProcessor attributeProcessor) {
		final var attr = attributeProcessor.modelAttribute;

		final String setter;
		if (!attributeProcessor.modelAttribute.isReadOnly()) {
			setter = attr.getDeclaringType().getSimpleName() + "::" + attributeProcessor.setter();
		} else {
			setter = "null";
		}
		return "Persisters.persister(%s::%s, %s)"
				.formatted(
					attr.getDeclaringType().getSimpleName(),
					attributeProcessor.getter(), setter);
	}

	@Override
	public String getPrimitiveFieldCreation(final PrimitiveProcessor attributeProcessor) {
		return getFieldCreation(attributeProcessor);
	}

	@Override
	public void addImports(AttributeProcessor attributeProcessor) {
		// noop
	}

}