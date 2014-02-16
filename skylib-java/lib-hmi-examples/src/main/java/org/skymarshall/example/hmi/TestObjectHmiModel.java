package org.skymarshall.example.hmi;

import org.skymarshall.hmi.mvc.HmiModel;
import org.skymarshall.hmi.mvc.IComponentBinding;
import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;
import org.skymarshall.hmi.mvc.properties.ErrorProperty;
import org.skymarshall.hmi.mvc.HmiController;
import org.skymarshall.hmi.mvc.properties.IntProperty;
import org.skymarshall.hmi.mvc.properties.Properties;
import org.skymarshall.hmi.mvc.properties.ObjectProperty;
import java.lang.reflect.Field;
import org.skymarshall.hmi.mvc.IComponentLink;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;
import java.lang.reflect.AccessibleObject;
import org.skymarshall.hmi.mvc.objectaccess.FieldAccess;
import org.skymarshall.hmi.mvc.IObjectHmiModel;

public class TestObjectHmiModel extends HmiModel implements IObjectHmiModel<org.skymarshall.example.hmi.TestObject> {
    public static final String AFIRST_VALUE = "AFirstValue";

    private static final Field AFIRST_VALUE_FIELD;

    public static final String ASECOND_VALUE = "ASecondValue";

    private static final Field ASECOND_VALUE_FIELD;

    static {
        try {
            ASECOND_VALUE_FIELD = org.skymarshall.example.hmi.TestObject.class.getDeclaredField("aSecondValue");
            AFIRST_VALUE_FIELD = org.skymarshall.example.hmi.TestObject.class.getDeclaredField("aFirstValue");
            AccessibleObject.setAccessible(new AccessibleObject[]{ASECOND_VALUE_FIELD, AFIRST_VALUE_FIELD}, true);
        } catch (final Exception e) {
            throw new IllegalStateException("Cannot initialize class", e);
        }
    }

    protected final ObjectProperty<java.lang.String> aFirstValueProperty;
    protected final IntProperty aSecondValueProperty;
    public TestObjectHmiModel(final String prefix, final ControllerPropertyChangeSupport propertySupport, final ErrorProperty errorProperty) {
        super(propertySupport, errorProperty);
        aFirstValueProperty = Properties.persistent(new ObjectProperty<java.lang.String>(prefix + "-AFirstValue",  propertySupport, errorProperty), FieldAccess.create(AFIRST_VALUE_FIELD, java.lang.String.class));
        aSecondValueProperty = Properties.persistent(new IntProperty(prefix + "-ASecondValue",  propertySupport, errorProperty), FieldAccess.intAccess(ASECOND_VALUE_FIELD));
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


    public ObjectProperty<java.lang.String> getAFirstValueProperty() {
        return aFirstValueProperty;
    }

    public IntProperty getASecondValueProperty() {
        return aSecondValueProperty;
    }

    @Override
    public void loadFrom(org.skymarshall.example.hmi.TestObject object) {
        aFirstValueProperty.loadFrom(this, object);
        aSecondValueProperty.loadFrom(this, object);
    }

    @Override
    public void saveInto(org.skymarshall.example.hmi.TestObject object) {
        aFirstValueProperty.saveInto(object);
        aSecondValueProperty.saveInto(object);
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
