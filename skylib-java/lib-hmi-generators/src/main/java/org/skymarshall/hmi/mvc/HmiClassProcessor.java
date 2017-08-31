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

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.skymarshall.util.dao.metadata.AbstractAttributeMetaData;
import org.skymarshall.util.dao.metadata.UntypedDataObjectMetaData;
import org.skymarshall.util.generators.JavaCodeGenerator;
import org.skymarshall.util.generators.Template;
import org.skymarshall.util.helpers.ClassLoaderHelper;

public class HmiClassProcessor {

	private static final String ATTRIB_PUBLIC = "public ";

	public static class Context {
		public Map<String, String> context = new HashMap<>();
		final Set<String> imports = new HashSet<>();

		public void addImport(final Class<?> class1) {
			imports.add(class1.getName());
		}

		public void append(final String key, final String value) {
			Template.append(context, key, value);
		}

		public void addImport(final String className) {
			imports.add(HmiModelGenerator.findClass(className).getName());
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

	private final Context context;

	public HmiClassProcessor(final Class<?> clazz) {
		this.clazz = clazz;
		this.context = new Context();
	}

	public String getClassName() {
		return clazz.getSimpleName() + "HmiModel";
	}

	protected boolean ignore(final AbstractAttributeMetaData<?> attrib) {
		return Modifier.isStatic(attrib.getModifier());
	}

	protected Template process() throws IOException {

		final UntypedDataObjectMetaData metaData = new UntypedDataObjectMetaData(clazz, true);

		final String strType = typeToString(metaData.getDataType());

		context.context.put("modelClass", getClassName());
		context.context.put("objectClass", strType);
		context.context.put("objectClassSimpleName", metaData.getDataType().getSimpleName());

		// attributes
		addAttributesDeclarations(metaData);

		addAttributesGetters(metaData);

		addAttributePersistenceMethods(metaData);

		context.context.put("imports", JavaCodeGenerator.toImports(context.imports));
		return new Template(ClassLoaderHelper.readUTF8Resource("templates/hmiModel.template")).apply(context.context);
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
		context.append("fields.initStatic", afterPreprocessAttribs());

		forEachAttribute(metaData, attrib -> context.append("fields.declare",
				FieldProcessor.create(context, attrib).addImport().generateDeclaration()));

		forEachAttribute(metaData, attrib -> context.append("fields.init",
				FieldProcessor.create(context, attrib).addImport().generateInitialization()));
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

	protected String generateLoadFrom(final AbstractAttributeMetaData<?> attrib) throws IOException {
		return FieldProcessor.create(context, attrib).getPropertyName() + ".load(this);";
	}

	protected String generateSaveInto(final AbstractAttributeMetaData<?> attrib) throws IOException {
		return FieldProcessor.create(context, attrib).getPropertyName() + ".save();";
	}

	protected String generateAccessConstants(final AbstractAttributeMetaData<?> attrib) throws IOException {
		final JavaCodeGenerator gen = new JavaCodeGenerator();
		final String constant = toConstant(attrib.getName());
		gen.appendIndentedLine("public static final String " + constant + " = \"" + attrib.getName() + "\";");
		gen.newLine();

		final String fieldConstant = constant + "_FIELD";
		fieldConstants.put(fieldConstant, attrib.getCodeName());
		gen.appendIndentedLine("private static final Field " + fieldConstant + ';');
		gen.newLine();

		return gen.toString();
	}

	protected String afterPreprocessAttribs() throws IOException {

		final JavaCodeGenerator gen = new JavaCodeGenerator();
		final StringBuilder fieldsList = new StringBuilder();

		for (final Map.Entry<String, String> entry : fieldConstants.entrySet()) {
			gen.appendIndentedLine(entry.getKey() + " = " + typeToString(clazz) + ".class.getDeclaredField(\""
					+ entry.getValue() + "\");");
			fieldsList.append(", ");
			fieldsList.append(entry.getKey());
		}

		gen.appendIndentedLine("AccessibleObject.setAccessible(new AccessibleObject[]{"
				+ fieldsList.toString().substring(2) + "}, true);");

		return gen.toString();
	}

	protected String generateGetter(final AbstractAttributeMetaData<?> attrib) throws IOException {
		final FieldProcessor processor = FieldProcessor.create(context, attrib);

		final JavaCodeGenerator gen = new JavaCodeGenerator();
		gen.openBlock(ATTRIB_PUBLIC, processor.getPropertyType(), " get", attrib.getName(), "Property()");
		gen.appendIndentedLine("return " + processor.getPropertyName() + ";");
		gen.closeBlock();
		return gen.toString();
	}
}