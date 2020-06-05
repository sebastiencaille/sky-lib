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
package ch.skymarshall.gui.mvc;

import static ch.skymarshall.util.generators.JavaCodeGenerator.toConstant;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ch.skymarshall.gui.mvc.AttributeProcessor.AttributeProcessorDelegate;
import ch.skymarshall.util.dao.metadata.AbstractAttributeMetaData;
import ch.skymarshall.util.dao.metadata.UntypedDataObjectMetaData;
import ch.skymarshall.util.generators.JavaCodeGenerator;
import ch.skymarshall.util.generators.Template;
import ch.skymarshall.util.helpers.ClassFinder;

public class ModelClassProcessor {

	private static final String ATTRIB_PUBLIC = "public ";

	private final boolean java8 = !Boolean.valueOf("preJava8");

	public static class Context {
		final Map<String, String> properties = new HashMap<>();
		final Set<String> imports = new HashSet<>();
		final Map<String, String> generatedConstants = new HashMap<>();

		public void addImport(final Class<?> class1) {
			imports.add(class1.getName());
		}

		public void append(final String key, final String value) {
			Template.append(properties, key, value);
		}

		public void addImport(final String className) {
			imports.add(ClassFinder.forApp().loadByName(className).getName());
		}

	}

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
		if (!(type instanceof ParameterizedType)) {
			throw new IllegalArgumentException("Unhandled type " + type);
		}
		final ParameterizedType p = (ParameterizedType) type;
		final StringBuilder builder = new StringBuilder();
		char sep = '<';
		for (final Type st : p.getActualTypeArguments()) {
			builder.append(sep).append(typeToString(st));
			sep = ',';
		}
		builder.append('>');
		return builder.toString();
	}

	private final Class<?> modelClass;

	private final Context context;

	private final AttributeProcessorDelegate delegate;

	public ModelClassProcessor(final Class<?> clazz) {
		this.modelClass = clazz;
		this.context = new Context();

		if (java8) {
			delegate = new AttributeProcessor.GetSetAttributeDelegate();
		} else {
			delegate = new AttributeProcessor.FieldAttributeDelegate();
		}
	}

	public String getClassName() {
		return modelClass.getSimpleName() + "GuiModel";
	}

	protected boolean includeAttribute(final AbstractAttributeMetaData<?> attrib) {
		return !Modifier.isStatic(attrib.getModifier());
	}

	protected Template process() throws IOException {

		final UntypedDataObjectMetaData metaData = new UntypedDataObjectMetaData(modelClass, false);

		final String strType = typeToString(metaData.getDataType());

		context.properties.put("modelClass", getClassName());
		context.properties.put("objectClass", strType);
		context.properties.put("objectClassSimpleName", metaData.getDataType().getSimpleName());

		// attributes
		addAttributesDeclarations(metaData);

		addAttributesGetters(metaData);

		addAttributePersistenceMethods(metaData);

		context.properties.put("imports", JavaCodeGenerator.toImports(context.imports));

		final String pkg = modelClass.getPackage().getName();
		context.properties.put("package", pkg);
		return Template.from("templates/guiModel.template").apply(context.properties,
				JavaCodeGenerator.classToSource(pkg, getClassName()));
	}

	protected void addAttributesGetters(final UntypedDataObjectMetaData metaData) throws IOException {
		forEachAttribute(metaData, attrib -> context.append("fields.getters", generateGetter(attrib)));
	}

	protected void addAttributePersistenceMethods(final UntypedDataObjectMetaData metaData) throws IOException {
		forEachAttribute(metaData, attrib -> context.append("fields.load", generateLoadFrom(attrib)));
		forEachAttribute(metaData, attrib -> context.append("fields.save", generateSaveInto(attrib)));
	}

	/**
	 * Creates attributes related constants and declarations
	 *
	 * @param gen
	 * @param metaData
	 * @throws IOException
	 */
	protected void addAttributesDeclarations(final UntypedDataObjectMetaData metaData) throws IOException {

		forEachAttribute(metaData, attrib -> context.append("fields.declareStatic", generateAccessConstants(attrib)));
		if (!java8) {
			forEachAttribute(metaData,
					attrib -> context.append("fields.declareStatic", generateFieldConstants(attrib)));
			context.append("fields.initStatic", afterPreprocessAttribs());
		} else {
			context.append("fields.initStatic", "");
		}

		forEachAttribute(metaData, attrib -> context.append("fields.declare",
				AttributeProcessor.create(context, attrib, delegate).addImports().generateDeclaration() + "\n"));

		forEachAttribute(metaData, attrib -> context.append("fields.init",
				AttributeProcessor.create(context, attrib, delegate).addImports().generateInitialization() + "\n"));
	}

	protected interface AttributeApplier {
		void apply(AbstractAttributeMetaData<?> attrib) throws IOException;
	}

	protected void forEachAttribute(final UntypedDataObjectMetaData metaData, final AttributeApplier attributeApplier)
			throws IOException {
		for (final AbstractAttributeMetaData<?> attrib : metaData.getAttributes().stream()
				.filter(this::includeAttribute).collect(toList())) {
			attributeApplier.apply(attrib);
		}
	}

	protected String generateLoadFrom(final AbstractAttributeMetaData<?> attrib) {
		return AttributeProcessor.create(context, attrib, delegate).getPropertyName() + ".load(this);";
	}

	protected String generateSaveInto(final AbstractAttributeMetaData<?> attrib) {
		return AttributeProcessor.create(context, attrib, delegate).getPropertyName() + ".save();";
	}

	protected String generateAccessConstants(final AbstractAttributeMetaData<?> attrib) throws IOException {
		final JavaCodeGenerator gen = new JavaCodeGenerator();
		gen.appendIndentedLine(
				"public static final String " + toConstant(attrib.getName()) + " = \"" + attrib.getName() + "\";");
		gen.eol();

		return gen.toString();
	}

	protected String generateFieldConstants(final AbstractAttributeMetaData<?> attrib) throws IOException {
		final JavaCodeGenerator gen = new JavaCodeGenerator();
		final String constant = toConstant(attrib.getName());
		final String fieldConstant = constant + "_FIELD";
		context.generatedConstants.put(fieldConstant, attrib.getCodeName());
		gen.appendIndentedLine("private static final Field " + fieldConstant + ';');
		gen.eol();

		return gen.toString();
	}

	protected String afterPreprocessAttribs() throws IOException {

		final JavaCodeGenerator gen = new JavaCodeGenerator();
		final StringBuilder fieldsList = new StringBuilder();

		for (final Map.Entry<String, String> entry : context.generatedConstants.entrySet()) {
			gen.appendIndentedLine(entry.getKey() + " = " + typeToString(modelClass) + ".class.getDeclaredField(\""
					+ entry.getValue() + "\");");
			fieldsList.append(", ");
			fieldsList.append(entry.getKey());
		}

		gen.appendIndentedLine("AccessibleObject.setAccessible(new AccessibleObject[]{"
				+ fieldsList.toString().substring(2) + "}, true);");

		return gen.toString();
	}

	protected String generateGetter(final AbstractAttributeMetaData<?> attrib) throws IOException {
		final AttributeProcessor processor = AttributeProcessor.create(context, attrib, delegate);

		final JavaCodeGenerator gen = new JavaCodeGenerator();
		gen.openBlock(ATTRIB_PUBLIC, processor.getPropertyType(), " get", attrib.getName(), "Property()");
		gen.appendIndentedLine("return " + processor.getPropertyName() + ";");
		gen.closeBlock();
		return gen.toString();
	}
}