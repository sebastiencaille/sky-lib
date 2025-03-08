package ch.scaille.gui.mvc;

import ch.scaille.gui.mvc.AttributeProcessor.PrimitiveProcessor;

public interface AttributeProcessorDelegate {

	String getFieldCreation(AttributeProcessor attributeProcessor);

	void addImports(AttributeProcessor attributeProcessor);

	String getPrimitiveFieldCreation(PrimitiveProcessor primitiveProcessor);

}