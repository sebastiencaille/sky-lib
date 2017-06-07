/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above copyright notice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.example.hmi.controller.impl;

import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;
import org.skymarshall.hmi.mvc.properties.BooleanProperty;
import org.skymarshall.hmi.mvc.HmiModel;
import org.skymarshall.hmi.mvc.IObjectHmiModel;
import java.lang.reflect.AccessibleObject;
import org.skymarshall.hmi.mvc.properties.ObjectProperty;
import org.skymarshall.hmi.mvc.persisters.ObjectProviderPersister;
import org.skymarshall.hmi.mvc.IComponentBinding;
import org.skymarshall.hmi.mvc.HmiController;
import java.lang.reflect.Field;
import org.skymarshall.hmi.mvc.properties.IntProperty;
import org.skymarshall.hmi.mvc.persisters.Persisters;
import org.skymarshall.hmi.mvc.persisters.FieldAccess;
import org.skymarshall.hmi.mvc.properties.Properties;
import org.skymarshall.hmi.mvc.properties.ErrorProperty;
import org.skymarshall.hmi.mvc.IComponentLink;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;

public class ControllerExampleObjectHmiModel extends HmiModel implements IObjectHmiModel<org.skymarshall.example.hmi.controller.impl.ControllerExampleObject> {
    public static final String BOOLEAN_PROP = "BooleanProp";

    private static final Field BOOLEAN_PROP_FIELD;

    public static final String INT_PROP = "IntProp";

    private static final Field INT_PROP_FIELD;

    public static final String TEST_OBJECT_PROP = "TestObjectProp";

    private static final Field TEST_OBJECT_PROP_FIELD;

    public static final String STRING_PROP = "StringProp";

    private static final Field STRING_PROP_FIELD;

    static {
        try {
            STRING_PROP_FIELD = org.skymarshall.example.hmi.controller.impl.ControllerExampleObject.class.getDeclaredField("stringProp");
            INT_PROP_FIELD = org.skymarshall.example.hmi.controller.impl.ControllerExampleObject.class.getDeclaredField("intProp");
            BOOLEAN_PROP_FIELD = org.skymarshall.example.hmi.controller.impl.ControllerExampleObject.class.getDeclaredField("booleanProp");
            TEST_OBJECT_PROP_FIELD = org.skymarshall.example.hmi.controller.impl.ControllerExampleObject.class.getDeclaredField("testObjectProp");
            AccessibleObject.setAccessible(new AccessibleObject[]{STRING_PROP_FIELD, INT_PROP_FIELD, BOOLEAN_PROP_FIELD, TEST_OBJECT_PROP_FIELD}, true);
        } catch (final Exception e) {
            throw new IllegalStateException("Cannot initialize class", e);
        }
    }
    private final ObjectProviderPersister.CurrentObjectProvider currentObjectProvider = new ObjectProviderPersister.CurrentObjectProvider();

    protected final BooleanProperty booleanPropProperty;
    protected final IntProperty intPropProperty;
    protected final ObjectProperty<org.skymarshall.example.hmi.TestObject> testObjectPropProperty;
    protected final ObjectProperty<java.lang.String> stringPropProperty;
    public ControllerExampleObjectHmiModel(final String prefix, final ControllerPropertyChangeSupport propertySupport, final ErrorProperty errorProperty) {
        super(propertySupport, errorProperty);
        booleanPropProperty = Properties.of(new BooleanProperty(prefix + "-BooleanProp",  propertySupport)).persistent(Persisters.from(currentObjectProvider, FieldAccess.booleanAccess(BOOLEAN_PROP_FIELD))).setErrorNotifier(errorProperty).getProperty();
        intPropProperty = Properties.of(new IntProperty(prefix + "-IntProp",  propertySupport)).persistent(Persisters.from(currentObjectProvider, FieldAccess.intAccess(INT_PROP_FIELD))).setErrorNotifier(errorProperty).getProperty();
        testObjectPropProperty = Properties.of(new ObjectProperty<org.skymarshall.example.hmi.TestObject>(prefix + "-TestObjectProp",  propertySupport)).persistent(Persisters.from(currentObjectProvider, FieldAccess.<org.skymarshall.example.hmi.TestObject>create(TEST_OBJECT_PROP_FIELD))).setErrorNotifier(errorProperty).getProperty();
        stringPropProperty = Properties.of(new ObjectProperty<java.lang.String>(prefix + "-StringProp",  propertySupport)).persistent(Persisters.from(currentObjectProvider, FieldAccess.<java.lang.String>create(STRING_PROP_FIELD))).setErrorNotifier(errorProperty).getProperty();
    }

    public ControllerExampleObjectHmiModel(final String prefix, final HmiController controller) {
        this(prefix, controller.getPropertySupport(), HmiModel.createErrorProperty(prefix + "-Error", controller.getPropertySupport()));
    }

    public ControllerExampleObjectHmiModel(final HmiController controller) {
        this("ControllerExampleObject", controller.getPropertySupport(), HmiModel.createErrorProperty("ControllerExampleObject-Error", controller.getPropertySupport()));
    }

    public ControllerExampleObjectHmiModel(final String prefix, final ControllerPropertyChangeSupport propertySupport) {
        this(prefix, propertySupport, HmiModel.createErrorProperty(prefix + "-Error", propertySupport));
    }

    public ControllerExampleObjectHmiModel(final ControllerPropertyChangeSupport propertySupport) {
        this("ControllerExampleObject", propertySupport, HmiModel.createErrorProperty("ControllerExampleObject-Error", propertySupport));
    }


    public BooleanProperty getBooleanPropProperty() {
        return booleanPropProperty;
    }

    public IntProperty getIntPropProperty() {
        return intPropProperty;
    }

    public ObjectProperty<org.skymarshall.example.hmi.TestObject> getTestObjectPropProperty() {
        return testObjectPropProperty;
    }

    public ObjectProperty<java.lang.String> getStringPropProperty() {
        return stringPropProperty;
    }

    @Override
    public void setCurrentObject(final org.skymarshall.example.hmi.controller.impl.ControllerExampleObject value) {
        currentObjectProvider.setObject(value);
    }

    @Override
    public void load() {
        booleanPropProperty.load(this);
        intPropProperty.load(this);
        testObjectPropProperty.load(this);
        stringPropProperty.load(this);
    }

    @Override
    public void save() {
        booleanPropProperty.save();
        intPropProperty.save();
        testObjectPropProperty.save();
        stringPropProperty.save();
    }

    public IComponentBinding<org.skymarshall.example.hmi.controller.impl.ControllerExampleObject> binding() {
        return new IComponentBinding<org.skymarshall.example.hmi.controller.impl.ControllerExampleObject>() {
            @Override
            public Object getComponent() {
                return ControllerExampleObjectHmiModel.this;
            }
            @Override
            public void addComponentValueChangeListener(final IComponentLink<org.skymarshall.example.hmi.controller.impl.ControllerExampleObject> link) {
                // nope
            }
            @Override
            public void setComponentValue(final AbstractProperty source, final org.skymarshall.example.hmi.controller.impl.ControllerExampleObject value) {
                if (value != null) {
                    setCurrentObject(value);
                    load();
                }
            }
        };
    }
}
