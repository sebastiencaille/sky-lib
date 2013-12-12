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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.skymarshall.JavaClassGenerator;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;
import org.skymarshall.hmi.mvc.properties.ListProperty;
import org.skymarshall.hmi.mvc.properties.MapProperty;
import org.skymarshall.hmi.mvc.properties.ObjectProperty;
import org.skymarshall.hmi.mvc.properties.SetProperty;
import org.skymarshall.util.dao.metadata.AbstractAttributeMetaData;

abstract class FieldProcessor {

    final AbstractAttributeMetaData<?> attrib;
    final JavaClassGenerator           gen;

    protected abstract String getPropertyType();

    protected abstract String getFieldCreation();

    abstract void addImport();

    public FieldProcessor(final JavaClassGenerator gen, final AbstractAttributeMetaData<?> attrib) {
        this.gen = gen;
        this.attrib = attrib;
    }

    String getPropertyName() {
        return gen.toFirstLetterInLowerCase(attrib.getName()) + "Property";
    }

    protected String typeToString() {
        return HmiClassProcessor.typeToString(attrib.getType());
    }

    static FieldProcessor create(final JavaClassGenerator gen, final AbstractAttributeMetaData<?> attrib) {
        final Class<?> type = attrib.getType();
        if (type.isPrimitive()) {
            return new PrimitiveProcessor(gen, attrib);
        } else if (Set.class.isAssignableFrom(type)) {
            return new SetProcessor(gen, attrib);
        } else if (Map.class.isAssignableFrom(type)) {
            return new MapProcessor(gen, attrib);
        } else if (List.class.isAssignableFrom(type)) {
            return new ListProcessor(gen, attrib);
        }
        return new ObjectProcessor(gen, attrib);

    }

    static class PrimitiveProcessor extends FieldProcessor {

        public PrimitiveProcessor(final JavaClassGenerator gen, final AbstractAttributeMetaData<?> attrib) {
            super(gen, attrib);
        }

        @Override
        protected String getPropertyType() {
            return gen.toFirstLetterInUpperCase(attrib.getType().getName() + "Property");
        }

        @Override
        protected String getFieldCreation() {
            return "FieldAccess.create(" + gen.toConstant(attrib.getName() + "Field") + ")";
        }

        @Override
        void addImport() {
            gen.addImport(AbstractProperty.class.getPackage().getName() + '.' + getPropertyType());
        }

    }

    static class SetProcessor extends FieldProcessor {

        public SetProcessor(final JavaClassGenerator gen, final AbstractAttributeMetaData<?> attrib) {
            super(gen, attrib);
        }

        @Override
        protected String getPropertyType() {
            return "SetProperty";
        }

        @Override
        protected String getFieldCreation() {
            return "FieldAccess.create(" + gen.toConstant(attrib.getName() + "Field") + ", " + typeToString()
                    + ".class)";
        }

        @Override
        protected void addImport() {
            gen.addImport(SetProperty.class);
        }

    }

    static class MapProcessor extends FieldProcessor {

        public MapProcessor(final JavaClassGenerator gen, final AbstractAttributeMetaData<?> attrib) {
            super(gen, attrib);
        }

        @Override
        protected String getPropertyType() {
            return "MapProperty";
        }

        @Override
        protected String getFieldCreation() {
            return "FieldAccess.create(" + gen.toConstant(attrib.getName() + "Field") + ", " + typeToString()
                    + ".class)";
        }

        @Override
        protected void addImport() {
            gen.addImport(MapProperty.class);
        }

    }

    static class ListProcessor extends FieldProcessor {

        public ListProcessor(final JavaClassGenerator gen, final AbstractAttributeMetaData<?> attrib) {
            super(gen, attrib);
        }

        @Override
        protected String getPropertyType() {
            return "ListProperty";
        }

        @Override
        protected String getFieldCreation() {
            return "FieldAccess.create(" + gen.toConstant(attrib.getName() + "Field") + ", " + typeToString()
                    + ".class)";
        }

        @Override
        void addImport() {
            gen.addImport(ListProperty.class);
        }

    }

    static class ObjectProcessor extends FieldProcessor {

        public ObjectProcessor(final JavaClassGenerator gen, final AbstractAttributeMetaData<?> attrib) {
            super(gen, attrib);
        }

        @Override
        void addImport() {
            gen.addImport(ObjectProperty.class);
        }

        @Override
        protected String getPropertyType() {
            return "ObjectProperty<" + typeToString() + ">";
        }

        @Override
        protected String getFieldCreation() {
            return "FieldAccess.create(" + gen.toConstant(attrib.getName() + "Field") + ", " + typeToString()
                    + ".class)";
        }

    }

    void generateDeclaration() throws IOException {
        gen.appendLine(String.format("protected final %s %s = new %s(\"%s\",  propertySupport, errorProperty, %s);",
                getPropertyType(), getPropertyName(), getPropertyType(), attrib.getName(), getFieldCreation()));
    }
}
