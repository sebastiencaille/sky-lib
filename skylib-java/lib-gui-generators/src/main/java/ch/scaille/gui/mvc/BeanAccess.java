package ch.scaille.gui.mvc;

import ch.scaille.gui.mvc.AttributeProcessor.PrimitiveProcessor;

public interface BeanAccess {

	String getFieldCreation(AttributeProcessor attributeProcessor);

	void addImports(AttributeProcessor attributeProcessor);

	String getPrimitiveFieldCreation(PrimitiveProcessor primitiveProcessor);


	class GetSetAttributeBeanAccess implements BeanAccess {
		@Override
		public String getFieldCreation(final AttributeProcessor attributeProcessor) {
			final var attr = attributeProcessor.modelAttribute;

			String setter;
			if (!attributeProcessor.modelAttribute.isReadOnly()) {
				setter = attr.getDeclaringType().getSimpleName() + "::" + attributeProcessor.setter();
			} else {
				setter = "null";
			}
			return String.format("Persisters.persister(%s::%s, %s)", attr.getDeclaringType().getSimpleName(),
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
}