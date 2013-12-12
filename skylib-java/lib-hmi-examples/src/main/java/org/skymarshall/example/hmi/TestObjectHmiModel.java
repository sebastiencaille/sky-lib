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

    protected final IntProperty aSecondValueProperty = new IntProperty("ASecondValue",  propertySupport, errorProperty, FieldAccess.create(ASECOND_VALUE_FIELD));
    protected final ObjectProperty<java.lang.String> aFirstValueProperty = new ObjectProperty<java.lang.String>("AFirstValue",  propertySupport, errorProperty, FieldAccess.create(AFIRST_VALUE_FIELD, java.lang.String.class));

    public TestObjectHmiModel(final HmiController controller) {
        super(controller);
    }

    public TestObjectHmiModel(final ControllerPropertyChangeSupport propertySupport) {
        super(propertySupport);
    }

    public TestObjectHmiModel(final ControllerPropertyChangeSupport propertySupport, final ErrorProperty errorProperty) {
        super(propertySupport, errorProperty);
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
