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

import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.skymarshall.JavaClassGenerator;
import org.skymarshall.hmi.mvc.persisters.FieldAccess;
import org.skymarshall.hmi.mvc.persisters.ObjectProviderPersister;
import org.skymarshall.hmi.mvc.persisters.Persisters;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;
import org.skymarshall.hmi.mvc.properties.ErrorProperty;
import org.skymarshall.hmi.mvc.properties.Properties;
import org.skymarshall.util.dao.metadata.AbstractAttributeMetaData;
import org.skymarshall.util.dao.metadata.UntypedDataObjectMetaData;

public class HmiClassProcessor {

	private static final String ATTRIB_PUBLIC = "public ";

	static String typeToString(final Type type) {
		if (type instanceof Class) {
			return ((Class<?>) type).getCanonicalName();
		} else if (type instanceof ParameterizedType) {

			final ParameterizedType p = (ParameterizedType) type;
			final StringBuilder builder = new StringBuilder(typeToString(p.getRawType()));
			char sep = '<';
			for (final Type st : p.getActualTypeArguments()) {
				builder.append(sep).append(typeToString(st));
				sep = ',';
			}
			builder.append('>');
			return builder.toString();
		} else {
			throw new IllegalArgumentException("Unhandled type " + type);
		}

	}

	static String typeParametersToString(final Type type) {
		if (type instanceof ParameterizedType) {
			final ParameterizedType p = (ParameterizedType) type;
			final StringBuilder builder = new StringBuilder();
			char sep = '<';
			for (final Type st : p.getActualTypeArguments()) {
				builder.append(sep).append(typeToString(st));
				sep = ',';
			}
			builder.append('>');
			return builder.toString();
		} else {
			throw new IllegalArgumentException("Unhandled type " + type);
		}

	}

	private final Class<?> clazz;

	private final Map<String, String> fieldConstants = new HashMap<>();

	private final String defaultPrefixInQuotes;

	public HmiClassProcessor(final Class<?> clazz) {
		this.clazz = clazz;
		this.defaultPrefixInQuotes = "\"" + clazz.getSimpleName() + "\"";

	}

	public String getClassName() {
		return clazz.getSimpleName() + "HmiModel";
	}

	private void beforePreprocessAttribs(final JavaClassGenerator gen) {
		gen.addImport(Field.class);
	}

	protected boolean ignore(final AbstractAttributeMetaData<?> attrib) {
		return Modifier.isStatic(attrib.getModifier());
	}

	protected void process(final JavaClassGenerator gen) throws IOException {

		final UntypedDataObjectMetaData metaData = new UntypedDataObjectMetaData(clazz, true);

		addDefaultImports(gen);

		final String strType = typeToString(metaData.getDataType());

		openClass(gen, strType);

		// persistence
		addPersistenceUtils(gen);
		gen.newLine();

		// attributes
		addAttributesDeclarations(gen, metaData);
		gen.newLine();

		addConstructor(gen, metaData);

		addAttributesGetters(gen, metaData);

		addAttributePersistenceMethods(gen, metaData);

		addCurrentObjectMethods(gen, strType);
		gen.closeBlock();
		gen.closeBlock();

	}

	protected void addCurrentObjectMethods(final JavaClassGenerator gen, final String strType) throws IOException {
		gen.newLine();
		gen.appendAtOverride();
		gen.openBlock("public void setCurrentObject(final " + strType + " value)");
		gen.appendIndentedLine("currentObjectProvider.setObject(value);");
		gen.closeBlock();
		gen.newLine();

		gen.addImport(IComponentLink.class);
		gen.addImport(IComponentBinding.class);
		gen.addImport(AbstractProperty.class);
		gen.openBlock("public IComponentBinding<", strType, "> binding()");
		gen.openBlock("return new IComponentBinding<" + strType + ">()");
		gen.appendAtOverride();
		gen.openBlock("public void addComponentValueChangeListener(final IComponentLink<", strType, "> link)");
		gen.appendIndentedLine("// nope");
		gen.closeBlock();
		gen.appendAtOverride();
		gen.openBlock("public void setComponentValue(final AbstractProperty source, final ", strType, " value)");
		gen.openBlock("if (value != null)");
		gen.appendIndentedLine("setCurrentObject(value);");
		gen.appendIndentedLine("load();");
		gen.closeBlock();
		gen.closeBlock();
		gen.closeBlock(";");
	}

	protected void addAttributesGetters(final JavaClassGenerator gen, final UntypedDataObjectMetaData metaData)
			throws IOException {
		forEachAttribute(metaData, attrib -> generateGetter(gen, attrib));
	}

	protected void addAttributePersistenceMethods(final JavaClassGenerator gen,
			final UntypedDataObjectMetaData metaData) throws IOException {
		gen.newLine();
		gen.appendAtOverride();
		gen.openBlock("public void load()");
		forEachAttribute(metaData, attrib -> processLoadFrom(gen, attrib));
		gen.closeBlock();

		gen.newLine();
		gen.appendAtOverride();
		gen.openBlock("public void save()");
		forEachAttribute(metaData, attrib -> processSaveInto(gen, attrib));
		gen.closeBlock();
	}

	protected void addConstructor(final JavaClassGenerator gen, final UntypedDataObjectMetaData metaData)
			throws IOException {
		gen.openBlock(ATTRIB_PUBLIC, getClassName(),
				"(final String prefix, final ControllerPropertyChangeSupport propertySupport, final ErrorProperty errorProperty)");
		gen.appendIndentedLine("super(propertySupport, errorProperty);");
		forEachAttribute(metaData, attrib -> FieldProcessor.create(gen, attrib).generateInitialization());
		gen.closeBlock();

		gen.newLine();

		gen.addImport(HmiController.class);
		gen.openBlock(ATTRIB_PUBLIC, getClassName(), "(final String prefix, final HmiController controller)");
		gen.appendIndentedLine(
				"this(prefix, controller.getPropertySupport(), HmiModel.createErrorProperty(prefix + \"-Error\", controller.getPropertySupport()));");
		gen.closeBlock();
		gen.newLine();

		gen.openBlock(ATTRIB_PUBLIC, getClassName(), "(final HmiController controller)");
		gen.appendIndentedLine(
				"this(" + defaultPrefixInQuotes + ", controller.getPropertySupport(), HmiModel.createErrorProperty(\""
						+ clazz.getSimpleName() + "-Error\", controller.getPropertySupport()));");
		gen.closeBlock();
		gen.newLine();

		gen.openBlock(ATTRIB_PUBLIC, getClassName(),
				"(final String prefix, final ControllerPropertyChangeSupport propertySupport)");
		gen.appendIndentedLine(
				"this(prefix, propertySupport, HmiModel.createErrorProperty(prefix + \"-Error\", propertySupport));");
		gen.closeBlock();
		gen.newLine();

		gen.openBlock(ATTRIB_PUBLIC, getClassName(), "(final ControllerPropertyChangeSupport propertySupport)");
		gen.appendIndentedLine("this(" + defaultPrefixInQuotes + ", propertySupport, HmiModel.createErrorProperty(\""
				+ clazz.getSimpleName() + "-Error\", propertySupport));");
		gen.closeBlock();
		gen.newLine();
	}

	protected void addPersistenceUtils(final JavaClassGenerator gen) throws IOException {
		gen.addImport(ObjectProviderPersister.class);
		gen.appendIndentedLine(
				"private final ObjectProviderPersister.CurrentObjectProvider currentObjectProvider = new ObjectProviderPersister.CurrentObjectProvider();");
	}

	protected void openClass(final JavaClassGenerator gen, final String strType) throws IOException {
		gen.openBlock("public class ", getClassName(), " extends HmiModel implements IObjectHmiModel<", strType, ">");
	}

	protected void addDefaultImports(final JavaClassGenerator gen) {
		gen.addImport(HmiModel.class);
		gen.addImport(IObjectHmiModel.class);
		gen.addImport(IComponentBinding.class);
		gen.addImport(ErrorProperty.class);
		gen.addImport(org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport.class);
		gen.addImport(Properties.class);
	}

	/**
	 * Creates attributes related constants and declarations
	 *
	 * @param gen
	 * @param metaData
	 * @throws IOException
	 */
	protected void addAttributesDeclarations(final JavaClassGenerator gen, final UntypedDataObjectMetaData metaData)
			throws IOException {
		beforePreprocessAttribs(gen);
		forEachAttribute(metaData, attrib -> preprocess(gen, attrib));
		afterPreprocessAttribs(gen);

		gen.addImport(Persisters.class);
		forEachAttribute(metaData, attrib -> FieldProcessor.create(gen, attrib).addImport().generateDeclaration());
	}

	@FunctionalInterface
	protected interface AttributeApplier {
		void apply(AbstractAttributeMetaData<?> attrib) throws IOException;
	}

	protected void forEachAttribute(final UntypedDataObjectMetaData metaData, final AttributeApplier attributeApplier)
			throws IOException {
		for (final AbstractAttributeMetaData<?> attrib : metaData.getAttributes()) {
			if (ignore(attrib)) {
				continue;
			}
			attributeApplier.apply(attrib);
		}

	}

	protected void processLoadFrom(final JavaClassGenerator gen, final AbstractAttributeMetaData<?> attrib)
			throws IOException {
		gen.appendIndentedLine(FieldProcessor.create(gen, attrib).getPropertyName() + ".load(this);");
	}

	protected void processSaveInto(final JavaClassGenerator gen, final AbstractAttributeMetaData<?> attrib)
			throws IOException {
		gen.appendIndentedLine(FieldProcessor.create(gen, attrib).getPropertyName() + ".save();");
	}

	protected void preprocess(final JavaClassGenerator gen, final AbstractAttributeMetaData<?> attrib)
			throws IOException {
		final String constant = gen.toConstant(attrib.getName());
		gen.appendIndentedLine("public static final String " + constant + " = \"" + attrib.getName() + "\";");
		gen.newLine();

		final String fieldConstant = constant + "_FIELD";
		fieldConstants.put(fieldConstant, attrib.getCodeName());
		gen.appendIndentedLine("private static final Field " + fieldConstant + ';');
		gen.newLine();
	}

	protected void afterPreprocessAttribs(final JavaClassGenerator gen) throws IOException {
		gen.openBlock("static");
		gen.openBlock("try");

		final StringBuilder list = new StringBuilder();
		for (final Map.Entry<String, String> entry : fieldConstants.entrySet()) {
			gen.appendIndentedLine(entry.getKey() + " = " + typeToString(clazz) + ".class.getDeclaredField(\""
					+ entry.getValue() + "\");");
			list.append(", ");
			list.append(entry.getKey());
		}

		gen.addImport(AccessibleObject.class);
		gen.appendIndentedLine(
				"AccessibleObject.setAccessible(new AccessibleObject[]{" + list.toString().substring(2) + "}, true);");
		gen.unindent();
		gen.appendIndentedLine("} catch (final Exception e) {");
		gen.indent();
		gen.appendIndentedLine("throw new IllegalStateException(\"Cannot initialize class\", e);");
		gen.closeBlock();
		gen.closeBlock();

		gen.addImport(FieldAccess.class);
	}

	protected void generateGetter(final JavaClassGenerator gen, final AbstractAttributeMetaData<?> attrib)
			throws IOException {
		gen.newLine();
		final FieldProcessor processor = FieldProcessor.create(gen, attrib);

		gen.openBlock(ATTRIB_PUBLIC, processor.getPropertyType(), " get", attrib.getName(), "Property()");
		gen.appendIndentedLine("return " + processor.getPropertyName() + ";");
		gen.closeBlock();

	}

}
