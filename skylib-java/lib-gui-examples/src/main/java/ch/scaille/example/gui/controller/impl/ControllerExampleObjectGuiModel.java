// File generated from template
package ch.scaille.example.gui.controller.impl;

import java.util.Arrays;
import ch.scaille.gui.mvc.GuiModel;
import ch.scaille.gui.mvc.IComponentBinding;
import ch.scaille.gui.mvc.IComponentLink;
import ch.scaille.gui.mvc.IObjectGuiModel;
import ch.scaille.gui.mvc.factories.Persisters;
import ch.scaille.gui.mvc.persisters.ObjectProviderPersister;
import ch.scaille.gui.mvc.properties.AbstractProperty;
import ch.scaille.gui.mvc.properties.Configuration;
import ch.scaille.gui.mvc.properties.BooleanProperty;
import ch.scaille.gui.mvc.properties.IntProperty;
import ch.scaille.gui.mvc.properties.ObjectProperty;


public class ControllerExampleObjectGuiModel extends GuiModel implements IObjectGuiModel<ControllerExampleObject> {
   
    private final ObjectProviderPersister.CurrentObjectProvider<ControllerExampleObject> currentObjectProvider = new ObjectProviderPersister.CurrentObjectProvider<>();

	public static final String BOOLEAN_PROP = "BooleanProp";
	
	public static final String INT_PROP = "IntProp";
	
	public static final String TEST_OBJECT_PROP = "TestObjectProp";
	
	public static final String STRING_PROP = "StringProp";
	
	

	protected final BooleanProperty booleanPropProperty;
	protected final IntProperty intPropProperty;
	protected final ObjectProperty<ch.scaille.example.gui.TestObject> testObjectPropProperty;
	protected final ObjectProperty<java.lang.String> stringPropProperty;
	
	
	protected final AbstractProperty[] allProperties;
	
    public ControllerExampleObjectGuiModel(final String prefix, ModelConfiguration config) {
		super(config.ifNotSet(()->	GuiModel.createErrorProperty(prefix + "ControllerExampleObject-Error", config)));
		booleanPropProperty = new BooleanProperty(prefix + BOOLEAN_PROP, this).configureTyped(
			Configuration.persistent(currentObjectProvider, Persisters.getSet(ControllerExampleObject::isBooleanProp, ControllerExampleObject::setBooleanProp)),
			implicitConverters(ControllerExampleObject.class, BOOLEAN_PROP, java.lang.Boolean.class));
		intPropProperty = new IntProperty(prefix + INT_PROP, this).configureTyped(
			Configuration.persistent(currentObjectProvider, Persisters.getSet(ControllerExampleObject::getIntProp, ControllerExampleObject::setIntProp)),
			implicitConverters(ControllerExampleObject.class, INT_PROP, java.lang.Integer.class));
		testObjectPropProperty = new ObjectProperty<ch.scaille.example.gui.TestObject>(prefix + TEST_OBJECT_PROP, this).configureTyped(
			Configuration.persistent(currentObjectProvider, Persisters.getSet(ControllerExampleObject::getTestObjectProp, ControllerExampleObject::setTestObjectProp)),
			implicitConverters(ControllerExampleObject.class, TEST_OBJECT_PROP, ch.scaille.example.gui.TestObject.class));
		stringPropProperty = new ObjectProperty<java.lang.String>(prefix + STRING_PROP, this).configureTyped(
			Configuration.persistent(currentObjectProvider, Persisters.getSet(ControllerExampleObject::getStringProp, ControllerExampleObject::setStringProp)),
			implicitConverters(ControllerExampleObject.class, STRING_PROP, java.lang.String.class));
		
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
	public ObjectProperty<ch.scaille.example.gui.TestObject> getTestObjectPropProperty() {
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
