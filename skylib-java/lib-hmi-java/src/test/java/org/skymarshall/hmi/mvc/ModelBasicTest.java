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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.skymarshall.hmi.TestObject;
import org.skymarshall.hmi.mvc.converters.AbstractObjectConverter;
import org.skymarshall.hmi.mvc.converters.ConversionException;
import org.skymarshall.hmi.mvc.objectaccess.FieldAccess;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;
import org.skymarshall.hmi.mvc.properties.ObjectProperty;

public class ModelBasicTest extends Assert {

    private class TestHmiModel extends HmiModel {
        public TestHmiModel(final HmiController controller) {
            super(controller);
        }

        private final ObjectProperty<Integer> integerProperty = new ObjectProperty<Integer>("IntegerProperty",
                                                                      propertySupport, errorProperty,
                                                                      FieldAccess.<Integer> create(TestObject.class,
                                                                              "val", Integer.class));

        private final ObjectProperty<String>  stringProperty  = new ObjectProperty<String>("StringProperty",
                                                                      propertySupport, errorProperty, null);
    }

    private HmiController controller;
    private TestHmiModel  model;

    @Before
    public void init() {
        controller = new HmiController(new ControllerPropertyChangeSupport(this, false));
        model = new TestHmiModel(controller);
    }

    private static class IntegerToStringConverter extends AbstractObjectConverter<Integer, String> {

        @Override
        protected String convertPropertyValueToComponentValue(final Integer propertyValue) {
            return String.valueOf(propertyValue);
        }

        @Override
        protected Integer convertComponentValueToPropertyValue(final String componentValue) throws ConversionException {
            try {
                return Integer.valueOf(componentValue);
            } catch (final NumberFormatException e) {
                errorNotifier.setError(getComponent(), HmiErrors.fromException(e));
                return getPropertyValue();
            }
        }

    }

    private static class TestBinding implements
            IComponentBinding<String> {

        private String                 value;
        private IComponentLink<String> converter;

        @Override
        public Object getComponent() {
            return "TestComponent";
        }

        @Override
        public void addComponentValueChangeListener(final IComponentLink<String> converter) {
            this.converter = converter;
        }

        public void setValue(final String value) {
            converter.setValueFromComponent(getComponent(), value);
        }

        @Override
        public void setComponentValue(final AbstractProperty source, final String value) {
            this.value = value;
        }

    }

    @Test
    public void testChain() {
        final TestBinding binding = new TestBinding();
        model.integerProperty.bind(new IntegerToStringConverter()).bind(binding);
        controller.start();

        final TestObject testObject = new TestObject(321);

        model.integerProperty.loadFrom(this, testObject);
        assertEquals("321", binding.value);

        model.integerProperty.setValue(this, Integer.valueOf(123));
        assertEquals("123", binding.value);

        binding.setValue("456");
        assertEquals(Integer.valueOf(456), model.integerProperty.getValue());

        model.integerProperty.saveInto(testObject);
        assertEquals(Integer.valueOf(456), testObject.val);

        // Test exception
        binding.setValue("bla");
        assertEquals(Integer.valueOf(456), model.integerProperty.getValue());
        assertNotNull(model.errorProperty.getValue().getContent());
        assertEquals(NumberFormatException.class, model.errorProperty.getValue().getContent().getClass());
    }

    @Test
    public void testIdentityChain() {
        final TestBinding binding = new TestBinding();
        model.stringProperty.bind(binding);
        controller.start();

        model.stringProperty.setValue(this, "123");
        assertEquals("123", binding.value);

        binding.setValue("456");
        assertEquals("456", model.stringProperty.getValue());
    }

}
