/*******************************************************************************
 * Copyright (c) 2013 Sebastien Caille.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms are permitted
 * provided that the above copyright notice and this paragraph are
 * duplicated in all such forms and that any documentation,
 * advertising materials, and other materials related to such
 * distribution and use acknowledge that the software was developed
 * by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.hmi.mvc;

import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.skymarshall.JavaClassGenerator;
import org.skymarshall.hmi.mvc.objectaccess.FieldAccess;
import org.skymarshall.hmi.mvc.properties.ErrorProperty;
import org.skymarshall.util.dao.metadata.AbstractAttributeMetaData;
import org.skymarshall.util.dao.metadata.UntypedDataObjectMetaData;

public class HmiClassProcessor {

    static String typeToString(final Class<?> clazz) {
        return clazz.getCanonicalName();
    }

    private final Class<?>            clazz;
    private final String              pkg;

    private final Map<String, String> fieldConstants = new HashMap<String, String>();
    private JavaClassGenerator        gen;

    public HmiClassProcessor(final Class<?> clazz, final String pkg) {
        this.clazz = clazz;
        this.pkg = pkg;
    }

    public String getClassName() {
        return clazz.getSimpleName() + "HmiModel";
    }

    private void beforePreprocessAttribs() {
        gen.addImport(Field.class);
    }

    protected boolean ignore(final AbstractAttributeMetaData<?> attrib) {
        return Modifier.isStatic(attrib.getModifier());
    }

    protected void process() throws IOException {

        final UntypedDataObjectMetaData metaData = new UntypedDataObjectMetaData(clazz, true);

        gen = new JavaClassGenerator();
        gen.setPackage(pkg);
        gen.addImport(HmiModel.class);
        gen.addImport(IObjectHmiModel.class);
        gen.addImport(ErrorProperty.class);
        gen.addImport(org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport.class);

        gen.appendLine("public class " + getClassName() + " extends HmiModel implements IObjectHmiModel<"
                + typeToString(metaData.getDataType()) + "> {");

        gen.indent();

        beforePreprocessAttribs();

        forEachAttribute(metaData, new AttributeApplier() {

            @Override
            public void apply(final AbstractAttributeMetaData<?> attrib) throws IOException {
                preprocess(attrib);
            }

        });

        afterPreprocessAttribs();

        gen.newLine();

        forEachAttribute(metaData, new AttributeApplier() {

            @Override
            public void apply(final AbstractAttributeMetaData<?> attrib) throws IOException {
                final FieldProcessor processor = FieldProcessor.create(gen, attrib);
                processor.addImport();
                processor.generateDeclaration();
            }

        });

        gen.newLine();
        gen.addImport(HmiController.class);
        gen.appendLine("public " + getClassName() + "(final HmiController controller) {");
        gen.indent();
        gen.appendLine("super(controller);");
        gen.unindent();
        gen.appendLine("}");
        gen.newLine();

        gen.appendLine("public " + getClassName() + "(final ControllerPropertyChangeSupport propertySupport) {");
        gen.indent();
        gen.appendLine("super(propertySupport);");
        gen.unindent();
        gen.appendLine("}");
        gen.newLine();

        gen.appendLine("public " + getClassName()
                + "(final ControllerPropertyChangeSupport propertySupport, final ErrorProperty errorProperty) {");
        gen.indent();
        gen.appendLine("super(propertySupport, errorProperty);");
        gen.unindent();
        gen.appendLine("}");
        gen.newLine();

        forEachAttribute(metaData, new AttributeApplier() {

            @Override
            public void apply(final AbstractAttributeMetaData<?> attrib) throws IOException {
                generateGetter(attrib);
            }

        });

        gen.newLine();
        gen.appendLine("@Override");
        gen.appendLine("public void loadFrom(" + typeToString(metaData.getDataType()) + " object) {");
        gen.indent();
        forEachAttribute(metaData, new AttributeApplier() {

            @Override
            public void apply(final AbstractAttributeMetaData<?> attrib) throws IOException {
                processLoadFrom(attrib);
            }
        });
        gen.unindent();
        gen.appendLine("}");

        gen.newLine();
        gen.appendLine("@Override");
        gen.appendLine("public void saveInto(" + typeToString(metaData.getDataType()) + " object) {");
        gen.indent();
        forEachAttribute(metaData, new AttributeApplier() {

            @Override
            public void apply(final AbstractAttributeMetaData<?> attrib) throws IOException {
                processSaveInto(attrib);
            }
        });
        gen.unindent();
        gen.appendLine("}");

        gen.unindent();
        gen.appendLine("}");
    }

    private interface AttributeApplier {
        void apply(AbstractAttributeMetaData<?> attrib) throws IOException;
    }

    private void forEachAttribute(final UntypedDataObjectMetaData metaData, final AttributeApplier attributeApplier)
            throws IOException {
        for (final AbstractAttributeMetaData<?> attrib : metaData.getAttributes()) {
            if (ignore(attrib)) {
                continue;
            }
            attributeApplier.apply(attrib);
        }

    }

    private void processLoadFrom(final AbstractAttributeMetaData<?> attrib) throws IOException {
        gen.appendLine(FieldProcessor.create(gen, attrib).getPropertyName() + ".loadFrom(this, object);");
    }

    private void processSaveInto(final AbstractAttributeMetaData<?> attrib) throws IOException {
        gen.appendLine(FieldProcessor.create(gen, attrib).getPropertyName() + ".saveInto(object);");
    }

    private void preprocess(final AbstractAttributeMetaData<?> attrib) throws IOException {
        final String constant = gen.toConstant(attrib.getName());
        gen.appendLine("public static final String " + constant + " = \"" + attrib.getName() + "\";");
        gen.newLine();

        final String fieldConstant = constant + "_FIELD";
        fieldConstants.put(fieldConstant, attrib.getCodeName());
        gen.appendLine("private static final Field " + fieldConstant + ';');
        gen.newLine();
    }

    private void afterPreprocessAttribs() throws IOException {
        gen.appendLine("static {");
        gen.indent();
        gen.appendLine("try {");
        gen.indent();

        final StringBuilder list = new StringBuilder();
        for (final Map.Entry<String, String> entry : fieldConstants.entrySet()) {
            gen.appendLine(entry.getKey() + " = " + typeToString(clazz) + ".class.getDeclaredField(\""
                    + entry.getValue() + "\");");
            list.append(", ");
            list.append(entry.getKey());
        }

        gen.addImport(AccessibleObject.class);
        gen.appendLine("AccessibleObject.setAccessible(new AccessibleObject[]{" + list.toString().substring(2)
                + "}, true);");
        gen.unindent();
        gen.appendLine("} catch (final Exception e) {");
        gen.indent();
        gen.appendLine("throw new IllegalStateException(\"Cannot initialize class\", e);");
        gen.unindent();
        gen.appendLine("}");
        gen.unindent();
        gen.appendLine("}");

        gen.addImport(FieldAccess.class);
    }

    protected void generateGetter(final AbstractAttributeMetaData<?> attrib) throws IOException {
        gen.newLine();
        final FieldProcessor processor = FieldProcessor.create(gen, attrib);

        gen.appendLine("public " + processor.getPropertyType() + " get" + attrib.getName() + "Property() {");
        gen.indent();
        gen.appendLine("return " + processor.getPropertyName() + ";");
        gen.unindent();
        gen.appendLine("}");

    }

    public String getOutput() {
        return gen.toString();
    }

}
