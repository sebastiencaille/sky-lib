// File generated from template
package org.skymarshall.example.hmi.controller.impl;

import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;
import org.skymarshall.hmi.mvc.HmiModel;
import org.skymarshall.hmi.mvc.IObjectHmiModel;
import org.skymarshall.hmi.mvc.persisters.ObjectProviderPersister;
import org.skymarshall.hmi.mvc.IComponentBinding;
import org.skymarshall.hmi.mvc.HmiController;
import org.skymarshall.hmi.mvc.persisters.Persisters;
import org.skymarshall.hmi.mvc.properties.Properties;
import org.skymarshall.hmi.mvc.properties.ErrorProperty;
import org.skymarshall.hmi.mvc.IComponentLink;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;
import org.skymarshall.hmi.mvc.properties.BooleanProperty;
import org.skymarshall.hmi.mvc.properties.ObjectProperty;
import org.skymarshall.hmi.mvc.persisters.GetSetAccess;
import org.skymarshall.hmi.mvc.properties.IntProperty;


public class ControllerExampleObjectHmiModel extends HmiModel implements IObjectHmiModel<org.skymarshall.example.hmi.controller.impl.ControllerExampleObject> {
   
    private final ObjectProviderPersister.CurrentObjectProvider currentObjectProvider = new ObjectProviderPersister.CurrentObjectProvider();

	public static final String BOOLEAN_PROP = "BooleanProp";
	
	public static final String INT_PROP = "IntProp";
	
	public static final String TEST_OBJECT_PROP = "TestObjectProp";
	
	public static final String STRING_PROP = "StringProp";
	
	

	protected final BooleanProperty booleanPropProperty;
	protected final IntProperty intPropProperty;
	protected final ObjectProperty<org.skymarshall.example.hmi.TestObject> testObjectPropProperty;
	protected final ObjectProperty<java.lang.String> stringPropProperty;
	

    public ControllerExampleObjectHmiModel(final String prefix, final ControllerPropertyChangeSupport propertySupport, final ErrorProperty errorProperty) {
        super(propertySupport, errorProperty);
		booleanPropProperty = Properties.of(new BooleanProperty(prefix + "-BooleanProp",  propertySupport)).persistent(Persisters.from(currentObjectProvider, GetSetAccess.<org.skymarshall.example.hmi.controller.impl.ControllerExampleObject,java.lang.Boolean>access((o) -> o::isBooleanProp, (o) ->o::setBooleanProp))).setErrorNotifier(errorProperty).getProperty();
		intPropProperty = Properties.of(new IntProperty(prefix + "-IntProp",  propertySupport)).persistent(Persisters.from(currentObjectProvider, GetSetAccess.<org.skymarshall.example.hmi.controller.impl.ControllerExampleObject,java.lang.Integer>access((o) -> o::getIntProp, (o) ->o::setIntProp))).setErrorNotifier(errorProperty).getProperty();
		testObjectPropProperty = Properties.of(new ObjectProperty<org.skymarshall.example.hmi.TestObject>(prefix + "-TestObjectProp",  propertySupport)).persistent(Persisters.from(currentObjectProvider, GetSetAccess.<org.skymarshall.example.hmi.controller.impl.ControllerExampleObject,org.skymarshall.example.hmi.TestObject>access((o) -> o::getTestObjectProp, (o) ->o::setTestObjectProp))).setErrorNotifier(errorProperty).getProperty();
		stringPropProperty = Properties.of(new ObjectProperty<java.lang.String>(prefix + "-StringProp",  propertySupport)).persistent(Persisters.from(currentObjectProvider, GetSetAccess.<org.skymarshall.example.hmi.controller.impl.ControllerExampleObject,java.lang.String>access((o) -> o::getStringProp, (o) ->o::setStringProp))).setErrorNotifier(errorProperty).getProperty();
		
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
    public void load() {
		booleanPropProperty.load(this);intPropProperty.load(this);testObjectPropProperty.load(this);stringPropProperty.load(this);
    }

    @Override
    public void save() {
		booleanPropProperty.save();intPropProperty.save();testObjectPropProperty.save();stringPropProperty.save();
    }

    @Override
    public void setCurrentObject(final org.skymarshall.example.hmi.controller.impl.ControllerExampleObject value) {
        currentObjectProvider.setObject(value);
    }

    public IComponentBinding<org.skymarshall.example.hmi.controller.impl.ControllerExampleObject> binding() {
        return new IComponentBinding<org.skymarshall.example.hmi.controller.impl.ControllerExampleObject>() {
        
            @Override
            public void addComponentValueChangeListener(final IComponentLink<org.skymarshall.example.hmi.controller.impl.ControllerExampleObject> link) {
                // nope
            }
            
            @Override
			public void removeComponentValueChangeListener() {
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
