/*******************************************************************************
 * Copyright (c) 2013 Sebastien Caille.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms are permitted
 * provided that the above copyright notice and this paragraph are
 * duplicated in all such forms and that any documentation,
 * advertising materials, and other materials related to such
 * distribution and use acknowledge that the software was developed
 * by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.hmi.mvc;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.skymarshall.hmi.mvc.properties.AbstractProperty;
import org.skymarshall.hmi.mvc.properties.AbstractTypedProperty;
import org.skymarshall.hmi.mvc.properties.ErrorNotifier;

/**
 * Contains the generic logic to bind the chain of links between a property and
 * a component.
 * <p>
 * Link is bound
 * <ul>
 * <li>"From" a property using a {@link PropertyBindingController}</li>
 * <li>"From" another link using a {@link LinkBindingController}</li>
 * <li>"To" a component binding using a {@link BindingToComponent}</li>
 * <li>"To" another link using a {@link BindingToLink}</li>
 * </ul>
 * 
 * This binding registers the "To" binding, and then asks the "To" binding to
 * register this binding as "From" binding
 * 
 * @author Sebastien Caille
 * 
 * @param <FromType>
 *            the type handled by the property-side's binding
 * @param <ToType>
 *            the type handled by the component-side's binding
 */
public abstract class AbstractLink<FromType, ToType> implements
        IComponentLink<ToType> {

    /**
     * The property-side binding.
     * <p>
     * 
     * @param <FromType>
     *            the type of the property-side
     */
    public interface BindingFrom<FromType> {
        void setPropertySideValue(final Object source, final FromType value);

        AbstractProperty getProperty();
    }

    /**
     * The component-side binding.
     * <p>
     * 
     * @param <ToType>
     *            the type handled by the component-side's binding
     */
    public interface BindingTo<ToType> {
        void setComponentSideValue(final AbstractProperty source, final ToType value);

        Object getComponent();
    }

    /**
     * Binding to a component binding.
     * <p>
     * 
     * @param <ToType>
     *            the type handled by the component-side's binding
     */
    class BindingToComponent implements
            BindingTo<ToType> {

        private final IComponentBinding<ToType> componentBinding;

        public BindingToComponent(final IComponentBinding<ToType> componentBinding) {
            this.componentBinding = componentBinding;
        }

        @Override
        public Object getComponent() {
            return componentBinding.getComponent();
        }

        @Override
        public void setComponentSideValue(final AbstractProperty source, final ToType value) {
            componentBinding.setComponentValue(source, value);
        }

        public BindingTo<ToType> bind() {
            componentBinding.addComponentValueChangeListener(AbstractLink.this);
            return this;
        }
    }

    /**
     * Component-side binding to another link
     * 
     * @param <NextToType>
     *            the type handled by the component-side's binding of the next
     *            binding (used to return a controller on the next binding)
     */
    class BindingToLink<NextToType> implements
            BindingTo<ToType> {
        private final AbstractLink<ToType, NextToType> link;

        public BindingToLink(final AbstractLink<ToType, NextToType> link) {
            this.link = link;
        }

        public IBindingController<NextToType> bind() {
            return link.bind(AbstractLink.this, errorNotifier);
        }

        @Override
        public void setComponentSideValue(final AbstractProperty source, final ToType value) {
            link.setValueFromProperty(source, value);
        }

        @Override
        public Object getComponent() {
            return link.getComponent();
        }

    }

    /**
     * Basic functionalities of the link controller.
     * <p>
     * 
     * @author Sebastien Caille
     * 
     */
    private abstract class AbstractLinkController implements
            IBindingController<ToType> {

        @Override
        public void bind(final IComponentBinding<ToType> newBinding) {
            if (bindingTo != null) {
                throw new RuntimeException("Already bound to " + bindingTo);
            }
            bindingTo = new BindingToComponent(newBinding).bind();
        }

        @Override
        public <NextToType> IBindingController<NextToType> bind(final AbstractLink<ToType, NextToType> link) {
            final BindingToLink<NextToType> binding = new BindingToLink<NextToType>(link);
            bindingTo = binding;
            return binding.bind();
        }

        @Override
        public AbstractProperty getProperty() {
            return bindingFrom.getProperty();
        }
    }

    /**
     * Controls the binding to the property.
     * <p>
     * Allows preventing the transmission of the value set into the property,
     * and restoring the component value when reattached. Useful when you want
     * to use the {@link BindingSelector}
     */
    private class PropertyBindingController extends AbstractLinkController implements
            BindingFrom<FromType> {

        private final AbstractTypedProperty<FromType> property;

        private PropertyChangeListener                listener;

        public PropertyBindingController(final AbstractTypedProperty<FromType> property) {
            this.property = property;
        }

        @Override
        public AbstractProperty getProperty() {
            return property;
        }

        /**
         * Puts back the property value into the component
         */
        @Override
        public void attach() {
            // System.out.println(getProperty().getName() + ":attaching "
            // + binding.getComponent().getClass().getSimpleName());
            transmit = true;
            reloadComponentValue();
        }

        /**
         * Prevents the update of the component
         */
        @Override
        public void detach() {
            // System.out.println(getProperty().getName() + ":detaching "
            // + binding.getComponent().getClass().getSimpleName());
            transmit = false;
        }

        @Override
        public void unbind() {
            property.removeListener(listener);
        }

        public PropertyBindingController bind() {
            listener = new PropertyChangeListener() {

                @Override
                public void propertyChange(final PropertyChangeEvent evt) {
                    if (transmit) {
                        setValueFromProperty(property, getPropertyValue());
                    }
                }

            };
            property.addListener(listener);
            return this;
        }

        @Override
        public void setPropertySideValue(final Object source, final FromType value) {
            property.setObjectValueFromComponent(source, value);
        }

    }

    /**
     * Controls the binding to another link.
     * <p>
     * 
     * @author Sebastien Caille
     * 
     */
    private class LinkBindingController extends AbstractLinkController implements
            BindingFrom<FromType> {
        private final AbstractLink<?, FromType> chain;

        public LinkBindingController(final AbstractLink<?, FromType> chain) {
            this.chain = chain;
        }

        @Override
        public void attach() {
            // no op
        }

        @Override
        public void detach() {
            // no op
        }

        @Override
        public void unbind() {
            // no op
        }

        @Override
        public AbstractProperty getProperty() {
            return chain.getProperty();
        }

        @Override
        public void setPropertySideValue(final Object source, final FromType value) {
            chain.setValueFromComponent(source, value);
        }
    }

    /**
     * Property side biding
     */
    protected BindingFrom<FromType> bindingFrom;

    /**
     * Component side binding
     */
    protected BindingTo<ToType>     bindingTo;

    private ErrorNotifier           errorNotifier;

    /**
     * if true, transmits the value of the property to the component
     */
    protected boolean               transmit = true;

    /**
     * Gets the value from the property
     * 
     * @return the value of the property
     */
    public abstract FromType getPropertyValue();

    /**
     * Sets the component side value from the property.
     * <p>
     * Normally calls bindingTo.setComponentSideValue
     * 
     * @param source
     * @param value
     */
    protected abstract void setValueFromProperty(AbstractProperty source, FromType value);

    /**
     * Sets the property-side value provided by the component.
     * <p>
     * Normally calls bindingFrom.setPropertySideValue
     * 
     * @param source
     * @param componentValue
     */
    @Override
    public abstract void setValueFromComponent(final Object source, final ToType componentValue);

    public AbstractProperty getProperty() {
        return bindingFrom.getProperty();
    }

    public AbstractLink() {
    }

    /**
     * Chains this link to a property.
     * 
     * @param aProperty
     *            the property this link is chained to
     * @param notifier
     *            an error notifier
     * @return a controller on this binding
     */
    protected IBindingController<ToType> bind(final AbstractTypedProperty<FromType> aProperty,
            final ErrorNotifier notifier) {
        this.errorNotifier = notifier;
        final PropertyBindingController controller = new PropertyBindingController(aProperty).bind();
        bindingFrom = controller;
        return controller;
    }

    /**
     * Chains a link to this link
     * <p>
     * 
     * @param link
     *            the chained link
     * @param notifier
     *            an error notifier
     * @return the controller on the link
     */
    public IBindingController<ToType> bind(final AbstractLink<?, FromType> link, final ErrorNotifier notifier) {
        this.errorNotifier = notifier;
        final LinkBindingController linkBindingController = new LinkBindingController(link);
        bindingFrom = linkBindingController;
        return linkBindingController;
    }

    protected ErrorNotifier getErrorNotifier() {
        return errorNotifier;
    }

    /**
     * Gets the component at the end of the chain
     * 
     * @return the component
     */
    public Object getComponent() {
        return bindingTo.getComponent();
    }

    /**
     * Reloads the property value into the component
     */
    @Override
    public void reloadComponentValue() {
        setValueFromProperty(bindingFrom.getProperty(), getPropertyValue());
    }

    /**
     * Makes the current binding listen to another property
     * 
     * @param property
     *            the property this binding will listen to
     * @return a controller on the binding
     */
    public PropertyBindingController listenToProperty(final AbstractTypedProperty<FromType> property) {
        return new PropertyBindingController(property).bind();
    }
}
