package org.skymarshall.example.hmi.controller.impl;

import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;
import org.skymarshall.hmi.mvc.properties.BooleanProperty;
import org.skymarshall.hmi.mvc.HmiModel;
import org.skymarshall.hmi.mvc.IObjectHmiModel;
import org.skymarshall.hmi.mvc.objectaccess.FieldAccess;
import java.lang.reflect.AccessibleObject;
import org.skymarshall.hmi.mvc.properties.ObjectProperty;
import org.skymarshall.hmi.mvc.properties.ErrorProperty;
import org.skymarshall.hmi.mvc.HmiController;
import java.lang.reflect.Field;
import org.skymarshall.hmi.mvc.properties.IntProperty;

public class ControllerExampleObjectHmiModel extends HmiModel implements IObjectHmiModel<org.skymarshall.example.hmi.controller.impl.ControllerExampleObject> {
    public static final String ASTRING_PROPERTY = "AStringProperty";

    private static final Field ASTRING_PROPERTY_FIELD;

    public static final String ABOOLEAN = "ABoolean";

    private static final Field ABOOLEAN_FIELD;

    public static final String ATEST_OBJECT_PROPERTY = "ATestObjectProperty";

    private static final Field ATEST_OBJECT_PROPERTY_FIELD;

    public static final String AN_INT_PROPERTY = "AnIntProperty";

    private static final Field AN_INT_PROPERTY_FIELD;

    static {
        try {
            AN_INT_PROPERTY_FIELD = org.skymarshall.example.hmi.controller.impl.ControllerExampleObject.class.getDeclaredField("anIntProperty");
            ASTRING_PROPERTY_FIELD = org.skymarshall.example.hmi.controller.impl.ControllerExampleObject.class.getDeclaredField("aStringProperty");
            ATEST_OBJECT_PROPERTY_FIELD = org.skymarshall.example.hmi.controller.impl.ControllerExampleObject.class.getDeclaredField("aTestObjectProperty");
            ABOOLEAN_FIELD = org.skymarshall.example.hmi.controller.impl.ControllerExampleObject.class.getDeclaredField("aBoolean");
            AccessibleObject.setAccessible(new AccessibleObject[]{AN_INT_PROPERTY_FIELD, ASTRING_PROPERTY_FIELD, ATEST_OBJECT_PROPERTY_FIELD, ABOOLEAN_FIELD}, true);
        } catch (final Exception e) {
            throw new IllegalStateException("Cannot initialize class", e);
        }
    }

    protected final ObjectProperty<java.lang.String> aStringPropertyProperty = new ObjectProperty<java.lang.String>("AStringProperty",  propertySupport, errorProperty, FieldAccess.create(ASTRING_PROPERTY_FIELD, java.lang.String.class));
    protected final BooleanProperty aBooleanProperty = new BooleanProperty("ABoolean",  propertySupport, errorProperty, FieldAccess.create(ABOOLEAN_FIELD));
    protected final ObjectProperty<org.skymarshall.example.hmi.TestObject> aTestObjectPropertyProperty = new ObjectProperty<org.skymarshall.example.hmi.TestObject>("ATestObjectProperty",  propertySupport, errorProperty, FieldAccess.create(ATEST_OBJECT_PROPERTY_FIELD, org.skymarshall.example.hmi.TestObject.class));
    protected final IntProperty anIntPropertyProperty = new IntProperty("AnIntProperty",  propertySupport, errorProperty, FieldAccess.create(AN_INT_PROPERTY_FIELD));

    public ControllerExampleObjectHmiModel(final HmiController controller) {
        super(controller);
    }

    public ControllerExampleObjectHmiModel(final ControllerPropertyChangeSupport propertySupport) {
        super(propertySupport);
    }

    public ControllerExampleObjectHmiModel(final ControllerPropertyChangeSupport propertySupport, final ErrorProperty errorProperty) {
        super(propertySupport, errorProperty);
    }


    public ObjectProperty<java.lang.String> getAStringPropertyProperty() {
        return aStringPropertyProperty;
    }

    public BooleanProperty getABooleanProperty() {
        return aBooleanProperty;
    }

    public ObjectProperty<org.skymarshall.example.hmi.TestObject> getATestObjectPropertyProperty() {
        return aTestObjectPropertyProperty;
    }

    public IntProperty getAnIntPropertyProperty() {
        return anIntPropertyProperty;
    }

    @Override
    public void loadFrom(org.skymarshall.example.hmi.controller.impl.ControllerExampleObject object) {
        aStringPropertyProperty.loadFrom(this, object);
        aBooleanProperty.loadFrom(this, object);
        aTestObjectPropertyProperty.loadFrom(this, object);
        anIntPropertyProperty.loadFrom(this, object);
    }

    @Override
    public void saveInto(org.skymarshall.example.hmi.controller.impl.ControllerExampleObject object) {
        aStringPropertyProperty.saveInto(object);
        aBooleanProperty.saveInto(object);
        aTestObjectPropertyProperty.saveInto(object);
        anIntPropertyProperty.saveInto(object);
    }
}
