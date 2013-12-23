package org.skymarshall.example.hmi;

import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;
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
    public TestObjectHmiModel(final String prefix, final ControllerPropertyChangeSupport propertySupport, final ErrorProperty errorProperty) {
        super(propertySupport, errorProperty);
        aSecondValueProperty = new IntProperty(prefix + "-ASecondValue",  propertySupport, errorProperty, FieldAccess.create(ASECOND_VALUE_FIELD));
        aFirstValueProperty = new ObjectProperty<java.lang.String>(prefix + "-AFirstValue",  propertySupport, errorProperty, FieldAccess.create(AFIRST_VALUE_FIELD, java.lang.String.class));
    }

    public TestObjectHmiModel(final String prefix, final HmiController controller) {
        this(prefix, controller.getPropertySupport(), HmiModel.createErrorProperty(prefix + "-Error", controller.getPropertySupport()));
    }

    public TestObjectHmiModel(final HmiController controller) {
        this("TestObject", controller.getPropertySupport(), HmiModel.createErrorProperty("TestObject-Error", controller.getPropertySupport()));
    }

    public TestObjectHmiModel(final String prefix, final ControllerPropertyChangeSupport propertySupport) {
        this(prefix, propertySupport, HmiModel.createErrorProperty(prefix + "-Error", propertySupport));
    }

    public TestObjectHmiModel(final ControllerPropertyChangeSupport propertySupport) {
        this("TestObject", propertySupport, HmiModel.createErrorProperty("TestObject-Error", propertySupport));
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

    public IComponentBinding<org.skymarshall.example.hmi.TestObject> binding() {
        return new IComponentBinding<org.skymarshall.example.hmi.TestObject>() {
            @Override
            public Object getComponent() {
                return TestObjectHmiModel.this;
            }
            @Override
            public void addComponentValueChangeListener(final IComponentLink<org.skymarshall.example.hmi.TestObject> link) {
                // nope
            }
            @Override
            public void setComponentValue(final AbstractProperty source, final org.skymarshall.example.hmi.TestObject value) {
                loadFrom(value);
            }
        };
    }
}
