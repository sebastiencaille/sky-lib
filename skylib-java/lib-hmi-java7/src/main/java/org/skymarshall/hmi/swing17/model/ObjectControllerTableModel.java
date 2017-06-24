/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
/*
 * Copyright (c) 2011, Caille Sebastien
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification,are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above Copyrightnotice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above Copyrightnotice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of the owner nor the names of its contributors may be
 *    used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE CopyrightHOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE CopyrightOWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.skymarshall.hmi.swing17.model;

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
 * @param <ModelType>
 * @param <ColumnsType>
 */
@SuppressWarnings("serial")
public abstract class ObjectControllerTableModel<ObjectType, ModelType extends IObjectHmiModel<ObjectType>, ColumnsType extends Enum<ColumnsType>>
        extends ListModelTableModel<ObjectType, ColumnsType> {

    static class TableBinding<ObjectType, U> implements
            IComponentBinding<U> {

        private final Map<ObjectType, U> changes = new HashMap<>();

        private AbstractProperty         property;

        private IComponentLink<U>        singleListener;

        private Object                   loadedValue;

        public TableBinding() {
        }

        @SuppressWarnings("unchecked")
        void addChange(final ObjectType object, final Object newValue) {
            changes.put(object, (U) newValue);
        }

        void commit(final Object object) {
            if (changes.containsKey(object)) {
                singleListener.setValueFromComponent(null, changes.get(object));
                property.save();
            }
        }

        Object getDisplayValue(final Object object) {
            if (changes.containsKey(object)) {
                return changes.get(object);
            }
            // This calls setComponentValue(...)
            property.load(this);
            return loadedValue;
        }

        @Override
        public void addComponentValueChangeListener(final IComponentLink<U> converter) {
            this.singleListener = converter;
        }

        @Override
        public void setComponentValue(final org.skymarshall.hmi.mvc.properties.AbstractProperty source, final U value) {
            this.loadedValue = value;
        }

        @Override
        public Object getComponent() {
            return null;
        }
    }

    private final TableBinding<ObjectType, ?>[] bindings;
    private final ModelType                     objectModel;

    /**
     * Binds all model properties with this model's bindings
     * 
     * @param aModel
     */
    protected abstract void bindModel(ModelType anObjectModel);

    protected abstract AbstractProperty getPropertyAt(ModelType anObjectModel, ColumnsType column);

    public ObjectControllerTableModel(final ListModel<ObjectType> listModel, final ModelType objectModel,
            final Class<ColumnsType> columnsEnumClass) {
        super(listModel, columnsEnumClass);
        this.objectModel = objectModel;
        bindings = new TableBinding[columnsEnumClass.getEnumConstants().length];
        bindModel(objectModel);

        for (final ColumnsType column : columnsEnumClass.getEnumConstants()) {
            bindings[column.ordinal()].property = getPropertyAt(objectModel, column);
        }
    }

    protected <U> IComponentBinding<U> getColumnBinding(final ColumnsType column) {
        final TableBinding<ObjectType, U> binding = new TableBinding<>();
        bindings[column.ordinal()] = binding;
        return binding;
    }

    @Override
    protected Object getValueAtColumn(final ObjectType object, final ColumnsType column) {
        final TableBinding<ObjectType, ?> binding = bindings[column.ordinal()];
        objectModel.setCurrentObject(object);
        return binding.getDisplayValue(object);
    }

    @Override
    protected void setValueAtColumn(final ObjectType object, final ColumnsType column, final Object value) {
        bindings[column.ordinal()].addChange(object, value);
    }

    public void commit() {
        final Set<ObjectType> modified = new HashSet<>();
        for (final TableBinding<ObjectType, ?> binding : bindings) {
            modified.addAll(binding.changes.keySet());
        }
        for (final ObjectType object : modified) {
            objectModel.setCurrentObject(object);
            for (final TableBinding<?, ?> binding : bindings) {
                binding.commit(object);
            }
        }
        for (final TableBinding<?, ?> binding : bindings) {
            binding.changes.clear();
        }
    }
}
