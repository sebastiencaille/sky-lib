package org.skymarshall.example.hmi.controller.impl;

import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;
import org.skymarshall.hmi.mvc.properties.BooleanProperty;
import org.skymarshall.hmi.mvc.HmiModel;
import org.skymarshall.hmi.mvc.IObjectHmiModel;
import org.skymarshall.hmi.mvc.objectaccess.FieldAccess;
import java.lang.reflect.AccessibleObject;
import org.skymarshall.hmi.mvc.properties.ObjectProperty;
import org.skymarshall.hmi.mvc.IComponentBinding;
import org.skymarshall.hmi.mvc.HmiController;
import java.lang.reflect.Field;
import org.skymarshall.hmi.mvc.properties.IntProperty;
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

    protected final BooleanProperty booleanPropProperty;
    protected final IntProperty intPropProperty;
    protected final ObjectProperty<org.skymarshall.example.hmi.TestObject> testObjectPropProperty;
    protected final ObjectProperty<java.lang.String> stringPropProperty;
    public ControllerExampleObjectHmiModel(final String prefix, final ControllerPropertyChangeSupport propertySupport, final ErrorProperty errorProperty) {
        super(propertySupport, errorProperty);
        booleanPropProperty = new BooleanProperty(prefix + "-BooleanProp",  propertySupport, errorProperty, FieldAccess.create(BOOLEAN_PROP_FIELD));
        intPropProperty = new IntProperty(prefix + "-IntProp",  propertySupport, errorProperty, FieldAccess.create(INT_PROP_FIELD));
        testObjectPropProperty = new ObjectProperty<org.skymarshall.example.hmi.TestObject>(prefix + "-TestObjectProp",  propertySupport, errorProperty, FieldAccess.create(TEST_OBJECT_PROP_FIELD, org.skymarshall.example.hmi.TestObject.class));
        stringPropProperty = new ObjectProperty<java.lang.String>(prefix + "-StringProp",  propertySupport, errorProperty, FieldAccess.create(STRING_PROP_FIELD, java.lang.String.class));
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
    public void loadFrom(org.skymarshall.example.hmi.controller.impl.ControllerExampleObject object) {
        booleanPropProperty.loadFrom(this, object);
        intPropProperty.loadFrom(this, object);
        testObjectPropProperty.loadFrom(this, object);
        stringPropProperty.loadFrom(this, object);
    }

    @Override
    public void saveInto(org.skymarshall.example.hmi.controller.impl.ControllerExampleObject object) {
        booleanPropProperty.saveInto(object);
        intPropProperty.saveInto(object);
        testObjectPropProperty.saveInto(object);
        stringPropProperty.saveInto(object);
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
                loadFrom(value);
            }
        };
    }
}
