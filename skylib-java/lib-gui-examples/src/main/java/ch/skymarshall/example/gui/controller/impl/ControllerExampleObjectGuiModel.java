// File generated from template
package ch.skymarshall.example.gui.controller.impl;

import java.util.Arrays;
import ch.skymarshall.gui.mvc.GuiModel;
import ch.skymarshall.gui.mvc.IComponentBinding;
import ch.skymarshall.gui.mvc.IComponentLink;
import ch.skymarshall.gui.mvc.IObjectGuiModel;
import ch.skymarshall.gui.mvc.factories.Persisters;
import ch.skymarshall.gui.mvc.persisters.ObjectProviderPersister;
import ch.skymarshall.gui.mvc.properties.AbstractProperty;
import ch.skymarshall.gui.mvc.properties.Configuration;
import ch.skymarshall.gui.mvc.properties.BooleanProperty;
import ch.skymarshall.gui.mvc.properties.IntProperty;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;


public class ControllerExampleObjectGuiModel extends GuiModel implements IObjectGuiModel<ControllerExampleObject> {
   
    private final ObjectProviderPersister.CurrentObjectProvider<ControllerExampleObject> currentObjectProvider = new ObjectProviderPersister.CurrentObjectProvider<>();

	public static final String BOOLEAN_PROP = "BooleanProp";
	
	public static final String INT_PROP = "IntProp";
	
	public static final String TEST_OBJECT_PROP = "TestObjectProp";
	
	public static final String STRING_PROP = "StringProp";
	
	

	protected final BooleanProperty booleanPropProperty;
	protected final IntProperty intPropProperty;
	protected final ObjectProperty<ch.skymarshall.example.gui.TestObject> testObjectPropProperty;
	protected final ObjectProperty<java.lang.String> stringPropProperty;
	
	
	protected final AbstractProperty[] allProperties;
	
    public ControllerExampleObjectGuiModel(final String prefix, ModelConfiguration config) {
		super(config.ifNotSet(()->	GuiModel.createErrorProperty(prefix + "ControllerExampleObject-Error", config)));
		booleanPropProperty = new BooleanProperty(prefix + "BooleanProp", this).configureTyped(Configuration.persistent(currentObjectProvider, Persisters.getSet(ControllerExampleObject::isBooleanProp, ControllerExampleObject::setBooleanProp)));
		config.getImplicitConverters().stream().sequential().map(c -> ((ImplicitConvertProvider<ControllerExampleObject, java.lang.Boolean>)c).create(ControllerExampleObject.class, "BooleanProp", java.lang.Boolean.class)).forEach(booleanPropProperty::addImplicitConverter);
		intPropProperty = new IntProperty(prefix + "IntProp", this).configureTyped(Configuration.persistent(currentObjectProvider, Persisters.getSet(ControllerExampleObject::getIntProp, ControllerExampleObject::setIntProp)));
		config.getImplicitConverters().stream().sequential().map(c -> ((ImplicitConvertProvider<ControllerExampleObject, java.lang.Integer>)c).create(ControllerExampleObject.class, "IntProp", java.lang.Integer.class)).forEach(intPropProperty::addImplicitConverter);
		testObjectPropProperty = new ObjectProperty<ch.skymarshall.example.gui.TestObject>(prefix + "TestObjectProp", this).configureTyped(Configuration.persistent(currentObjectProvider, Persisters.getSet(ControllerExampleObject::getTestObjectProp, ControllerExampleObject::setTestObjectProp)));
		config.getImplicitConverters().stream().sequential().map(c -> ((ImplicitConvertProvider<ControllerExampleObject, ch.skymarshall.example.gui.TestObject>)c).create(ControllerExampleObject.class, "TestObjectProp", ch.skymarshall.example.gui.TestObject.class)).forEach(testObjectPropProperty::addImplicitConverter);
		stringPropProperty = new ObjectProperty<java.lang.String>(prefix + "StringProp", this).configureTyped(Configuration.persistent(currentObjectProvider, Persisters.getSet(ControllerExampleObject::getStringProp, ControllerExampleObject::setStringProp)));
		config.getImplicitConverters().stream().sequential().map(c -> ((ImplicitConvertProvider<ControllerExampleObject, java.lang.String>)c).create(ControllerExampleObject.class, "StringProp", java.lang.String.class)).forEach(stringPropProperty::addImplicitConverter);
		
		allProperties = new AbstractProperty[]{booleanPropProperty, intPropProperty, testObjectPropProperty, stringPropProperty};
    }
            
    public ControllerExampleObjectGuiModel(ModelConfiguration config) {
    	this("", config);
    }

	public Class<?> getContainerClass() {
		return ControllerExampleObject.class;
	}

	public BooleanProperty getBooleanPropProperty() {
	    return booleanPropProperty;
	}
	public IntProperty getIntPropProperty() {
	    return intPropProperty;
	}
	public ObjectProperty<ch.skymarshall.example.gui.TestObject> getTestObjectPropProperty() {
	    return testObjectPropProperty;
	}
	public ObjectProperty<java.lang.String> getStringPropProperty() {
	    return stringPropProperty;
	}
	

    @Override
    public void load() {
    	try {
    		getPropertySupport().transmitAllToComponentOnly();
			Arrays.stream(allProperties).forEach(p -> p.load(this));
		} finally {
			getPropertySupport().enableAllTransmit();
		}
    }

    @Override
    public void save() {
		Arrays.stream(allProperties).forEach(AbstractProperty::save);
    }

    @Override
    public void setCurrentObject(final ControllerExampleObject value) {
        currentObjectProvider.setObject(value);
    }

    public IComponentBinding<ControllerExampleObject> loadBinding() {
        return new IComponentBinding<ControllerExampleObject>() {
        
            @Override
            public void addComponentValueChangeListener(final IComponentLink<ControllerExampleObject> link) {
                // nope
            }
            
            @Override
			public void removeComponentValueChangeListener() {
				  // nope
			}
            
            @Override
            public void setComponentValue(final AbstractProperty source, final ControllerExampleObject value) {
                if (value != null) {
                    setCurrentObject(value);
                    load();
                }
            }
        };
    }
}
