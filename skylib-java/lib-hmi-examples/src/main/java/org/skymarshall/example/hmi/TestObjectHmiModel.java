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
package org.skymarshall.example.hmi;

import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;
import org.skymarshall.hmi.mvc.HmiModel;
import org.skymarshall.hmi.mvc.IObjectHmiModel;
import org.skymarshall.hmi.mvc.objectaccess.FieldAccess;
import java.lang.reflect.AccessibleObject;
import org.skymarshall.hmi.mvc.properties.ObjectProperty;
import org.skymarshall.hmi.mvc.properties.ErrorProperty;
import org.skymarshall.hmi.mvc.HmiController;
import java.lang.reflect.Field;
import org.skymarshall.hmi.mvc.properties.IntProperty;

public class TestObjectHmiModel extends HmiModel implements IObjectHmiModel<org.skymarshall.example.hmi.TestObject> {
    public static final String ASECOND_VALUE = "ASecondValue";

    private static final Field ASECOND_VALUE_FIELD;

    public static final String AFIRST_VALUE = "AFirstValue";

    private static final Field AFIRST_VALUE_FIELD;

    static {
        try {
            AFIRST_VALUE_FIELD = org.skymarshall.example.hmi.TestObject.class.getDeclaredField("aFirstValue");
            ASECOND_VALUE_FIELD = org.skymarshall.example.hmi.TestObject.class.getDeclaredField("aSecondValue");
            AccessibleObject.setAccessible(new AccessibleObject[]{AFIRST_VALUE_FIELD, ASECOND_VALUE_FIELD}, true);
        } catch (final Exception e) {
            throw new IllegalStateException("Cannot initialize class", e);
        }
    }

    protected final IntProperty aSecondValueProperty;
    protected final ObjectProperty<java.lang.String> aFirstValueProperty;

    public TestObjectHmiModel(final HmiController controller) {
        this(controller.getPropertySupport(), controller.getErrorProperty());
    }

    public TestObjectHmiModel(final ControllerPropertyChangeSupport propertySupport, final ErrorProperty errorProperty) {
        super(propertySupport, errorProperty);
        aSecondValueProperty = new IntProperty("ASecondValue", propertySupport, errorProperty, FieldAccess.create(ASECOND_VALUE_FIELD));
        aFirstValueProperty = new ObjectProperty<java.lang.String>("AFirstValue", propertySupport, errorProperty, FieldAccess.create(AFIRST_VALUE_FIELD, java.lang.String.class));
    }


    public IntProperty getASecondValueProperty() {
        return aSecondValueProperty;
    }

    public ObjectProperty<java.lang.String> getAFirstValueProperty() {
        return aFirstValueProperty;
    }

    @Override
    public void loadFrom(org.skymarshall.example.hmi.TestObject object) {
        aSecondValueProperty.loadFrom(this, object);
        aFirstValueProperty.loadFrom(this, object);
    }

    @Override
    public void saveInto(org.skymarshall.example.hmi.TestObject object) {
        aSecondValueProperty.saveInto(object);
        aFirstValueProperty.saveInto(object);
    }
}
