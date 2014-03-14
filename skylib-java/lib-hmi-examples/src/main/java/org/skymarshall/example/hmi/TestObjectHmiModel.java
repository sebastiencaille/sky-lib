package org.skymarshall.example.hmi;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;
import org.skymarshall.hmi.mvc.HmiController;
import org.skymarshall.hmi.mvc.HmiModel;
import org.skymarshall.hmi.mvc.IComponentBinding;
import org.skymarshall.hmi.mvc.IComponentLink;
import org.skymarshall.hmi.mvc.IObjectHmiModel;
import org.skymarshall.hmi.mvc.persisters.FieldAccess;
import org.skymarshall.hmi.mvc.persisters.ObjectProviderPersister;
import org.skymarshall.hmi.mvc.persisters.Persisters;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;
import org.skymarshall.hmi.mvc.properties.ErrorProperty;
import org.skymarshall.hmi.mvc.properties.IntProperty;
import org.skymarshall.hmi.mvc.properties.ObjectProperty;
import org.skymarshall.hmi.mvc.properties.Properties;

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
    private final ObjectProviderPersister.CurrentObjectProvider currentObjectProvider = new ObjectProviderPersister.CurrentObjectProvider();

    protected final ObjectProperty<java.lang.String> aFirstValueProperty;
    protected final IntProperty aSecondValueProperty;
    public TestObjectHmiModel(final String prefix, final ControllerPropertyChangeSupport propertySupport, final ErrorProperty errorProperty) {
        super(propertySupport, errorProperty);
        aFirstValueProperty = Properties.of(new ObjectProperty<java.lang.String>(prefix + "-AFirstValue",  propertySupport)).persistent(Persisters.from(currentObjectProvider, FieldAccess.<java.lang.String>create(AFIRST_VALUE_FIELD))).setErrorNotifier(errorProperty).getProperty();
        aSecondValueProperty = Properties.of(new IntProperty(prefix + "-ASecondValue",  propertySupport)).persistent(Persisters.from(currentObjectProvider, FieldAccess.intAccess(ASECOND_VALUE_FIELD))).setErrorNotifier(errorProperty).getProperty();
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
    public void setCurrentObject(final org.skymarshall.example.hmi.TestObject value) {
        currentObjectProvider.setObject(value);
    }

    @Override
    public void load() {
        aFirstValueProperty.load(this);
        aSecondValueProperty.load(this);
    }

    @Override
    public void save() {
        aFirstValueProperty.save();
        aSecondValueProperty.save();
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
                if (value != null) {
                    setCurrentObject(value);
                    load();
                }
            }
        };
    }
}
