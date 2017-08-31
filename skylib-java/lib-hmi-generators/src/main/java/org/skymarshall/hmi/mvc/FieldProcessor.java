/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.hmi.mvc;

import static org.skymarshall.util.generators.JavaCodeGenerator.toConstant;
import static org.skymarshall.util.generators.JavaCodeGenerator.toFirstLetterInLowerCase;
import static org.skymarshall.util.generators.JavaCodeGenerator.toFirstLetterInUpperCase;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.skymarshall.hmi.mvc.HmiClassProcessor.Context;
import org.skymarshall.util.dao.metadata.AbstractAttributeMetaData;

abstract class FieldProcessor {

	final AbstractAttributeMetaData<?> attrib;
	final Context context;

	protected abstract String getPropertyType();

	abstract FieldProcessor addImport();

	public FieldProcessor(final Context context, final AbstractAttributeMetaData<?> attrib) {
		this.context = context;
		this.attrib = attrib;
	}

	String getPropertyName() {
		return toFirstLetterInLowerCase(attrib.getName()) + "Property";
	}

	protected String getFieldCreation() {
		return "FieldAccess.<" + getTypeAsString() + ">create(" + toConstant(attrib.getName()) + "_FIELD)";
	}

	protected String getTypeAsString() {
		return HmiClassProcessor.typeToString(attrib.getGenericType());
	}

	static FieldProcessor create(final Context context, final AbstractAttributeMetaData<?> attrib) {
		final Class<?> type = attrib.getType();
		if (type.isPrimitive()) {
			return new PrimitiveProcessor(context, attrib);
		} else if (Set.class.isAssignableFrom(type)) {
			return new SetProcessor(context, attrib);
		} else if (Map.class.isAssignableFrom(type)) {
			return new MapProcessor(context, attrib);
		} else if (List.class.isAssignableFrom(type)) {
			return new ListProcessor(context, attrib);
		}
		return new ObjectProcessor(context, attrib);

	}

	static class PrimitiveProcessor extends FieldProcessor {

		public PrimitiveProcessor(final Context context, final AbstractAttributeMetaData<?> attrib) {
			super(context, attrib);
		}

		@Override
		protected String getPropertyType() {
			return toFirstLetterInUpperCase(attrib.getType().getName() + "Property");
		}

		@Override
		protected String getFieldCreation() {
			return "FieldAccess." + attrib.getType().getName() + "Access(" + toConstant(attrib.getName()) + "_FIELD)";
		}

		@Override
		FieldProcessor addImport() {
			context.addImport(getPropertyType());
			return this;
		}

	}

	static class ContainerProcessorWithType extends FieldProcessor {

		private final String objectName;

		public ContainerProcessorWithType(final Context context, final AbstractAttributeMetaData<?> attrib,
				final String objectName) {
			super(context, attrib);
			this.objectName = objectName;
		}

		@Override
		protected String getPropertyType() {
			return objectName + HmiClassProcessor.typeParametersToString(attrib.getGenericType());
		}

		@Override
		protected FieldProcessor addImport() {
			context.addImport(objectName);
			return this;
		}

		@Override
		String generateInitialization() throws IOException {
			return generateInitializationWithType();
		}
	}

	static class SetProcessor extends ContainerProcessorWithType {

		public SetProcessor(final Context context, final AbstractAttributeMetaData<?> attrib) {
			super(context, attrib, "SetProperty");
		}

	}

	static class MapProcessor extends ContainerProcessorWithType {

		public MapProcessor(final Context context, final AbstractAttributeMetaData<?> attrib) {
			super(context, attrib, "MapProperty");
		}
	}

	static class ListProcessor extends ContainerProcessorWithType {

		public ListProcessor(final Context context, final AbstractAttributeMetaData<?> attrib) {
			super(context, attrib, "ListProperty");
		}
	}

	static class ObjectProcessor extends FieldProcessor {

		public ObjectProcessor(final Context context, final AbstractAttributeMetaData<?> attrib) {
			super(context, attrib);
		}

		@Override
		FieldProcessor addImport() {
			context.addImport("ObjectProperty");
			return this;
		}

		@Override
		protected String getPropertyType() {
			return "ObjectProperty<" + getTypeAsString() + ">";
		}

	}

	String generateDeclaration() throws IOException {
		return String.format("protected final %s %s;", getPropertyType(), getPropertyName());
	}

	String generateInitializationWithType() throws IOException {
		return String.format(
				"%s = Properties.<%s, %s>of(new %s(prefix + \"-%s\",  propertySupport)).persistent(Persisters.from(currentObjectProvider, %s)).setErrorNotifier(errorProperty).getProperty();",
				getPropertyName(), getTypeAsString(), getPropertyType(), getPropertyType(), attrib.getName(),
				getFieldCreation());
	}

	String generateInitialization() throws IOException {
		return String.format(
				"%s = Properties.of(new %s(prefix + \"-%s\",  propertySupport)).persistent(Persisters.from(currentObjectProvider, %s)).setErrorNotifier(errorProperty).getProperty();",
				getPropertyName(), getPropertyType(), attrib.getName(), getFieldCreation());
	}

}
