/*
 * Copyright (c) 2011, Caille Sebastien
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification,are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of the owner nor the names of its contributors may be
 *    used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.skymarshall.hmi.swing16.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.skymarshall.hmi.model.ListModel;
import org.skymarshall.hmi.mvc.IComponentBinding;
import org.skymarshall.hmi.mvc.IComponentLink;
import org.skymarshall.hmi.mvc.IObjectHmiModel;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;

/**
 * Table model that is using an object controller per column.
 * <p>
 * 
 * @author Sebastien Caille
 * 
 * @param <ObjectType>
 * @param <ControllerType>
 * @param <ColumnsType>
 */
@SuppressWarnings("serial")
public abstract class ObjectControllerTableModel<ObjectType, ControllerType extends IObjectHmiModel<ObjectType>, ColumnsType extends Enum<ColumnsType>>
        extends ListModelTableModel<ObjectType, ColumnsType> {

    static class TableBinding<U> implements
            IComponentBinding<U> {

        private final Map<Object, U> changes = new HashMap<Object, U>();

        private AbstractProperty     property;
        private Object               value;

        private IComponentLink<U>    converter;

        public TableBinding() {
        }

        public Object getValue() {
            return value;
        }

        @SuppressWarnings("unchecked")
        void addChange(final Object object, final Object newValue) {
            changes.put(object, (U) newValue);
        }

        void commit(final Object object) {
            if (changes.containsKey(object)) {
                converter.setValueFromComponent(null, changes.get(object));
                property.saveInto(object);
            }
        }

        Object getDisplayValue(final Object object) {
            if (changes.containsKey(object)) {
                return changes.get(object);
            }
            property.loadFrom(this, object);
            return value;
        }

        @Override
        public void addComponentValueChangeListener(final IComponentLink<U> converter) {
            this.converter = converter;
        }

        @Override
        public void setComponentValue(final org.skymarshall.hmi.mvc.properties.AbstractProperty source, final U value) {
            this.value = value;
        }

        @Override
        public Object getComponent() {
            return null;
        }
    }

    private final TableBinding<?>[] bindings;

    /**
     * Binds all model properties with this model's bindings
     * 
     * @param aController
     */
    protected abstract void bindController(ControllerType aController);

    protected abstract AbstractProperty getPropertyAt(ControllerType aController, ColumnsType column);

    public ObjectControllerTableModel(final ListModel<ObjectType> model, final ControllerType controller,
            final Class<ColumnsType> columnsEnumClass) {
        super(model, columnsEnumClass);

        bindings = new TableBinding<?>[columnsEnumClass.getEnumConstants().length];
        bindController(controller);
        for (final ColumnsType column : columnsEnumClass.getEnumConstants()) {
            bindings[column.ordinal()].property = getPropertyAt(controller, column);
        }
    }

    protected <U> IComponentBinding<U> getColumnBinding(final ColumnsType column) {
        final TableBinding<U> binding = new TableBinding<U>();
        bindings[column.ordinal()] = binding;
        return binding;
    }

    @Override
    protected Object getValueAtColumn(final ObjectType object, final ColumnsType column) {
        final TableBinding<?> binding = bindings[column.ordinal()];
        return binding.getDisplayValue(object);
    }

    @Override
    protected void setValueAtColumn(final ObjectType object, final ColumnsType column, final Object value) {
        bindings[column.ordinal()].addChange(object, value);
    }

    public void commit() {
        final Set<Object> modified = new HashSet<Object>();
        for (final TableBinding<?> binding : bindings) {
            modified.addAll(binding.changes.keySet());
        }
        for (final Object object : modified) {
            for (final TableBinding<?> binding : bindings) {
                binding.commit(object);
            }
        }
        for (final TableBinding<?> binding : bindings) {
            binding.changes.clear();
        }
    }
}
