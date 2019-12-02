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
package ch.skymarshall.gui.swing.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ch.skymarshall.gui.model.IEdition;
import ch.skymarshall.gui.model.ListModel;
import ch.skymarshall.gui.mvc.IComponentBinding;
import ch.skymarshall.gui.mvc.IComponentLink;
import ch.skymarshall.gui.mvc.IObjectGuiModel;
import ch.skymarshall.gui.mvc.properties.AbstractProperty;

/**
 * Table model that is using an object controller per column.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <O> type of the displayed object
 * @param <M> type of the object's gui model
 * @param <C> type that enums the columns
 */
@SuppressWarnings("serial")
public abstract class ObjectControllerTableModel<O, M extends IObjectGuiModel<O>, C extends Enum<C>>
		extends ListModelTableModel<O, C> {

	/**
	 *
	 * @author scaille
	 *
	 * @param <O> type of the displayed object
	 * @param <U> type of the column's data
	 */
	static class TableBinding<O, U> implements IComponentBinding<U> {

		private final Map<O, U> changes = new HashMap<>();

		private AbstractProperty property;

		private IComponentLink<U> singleListener;

		private Object loadedValue;

		@SuppressWarnings("unchecked")
		void addChange(final O object, final Object newValue) {
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
		public void removeComponentValueChangeListener() {
			this.singleListener = null;
		}

		@Override
		public void setComponentValue(final AbstractProperty source, final U value) {
			this.loadedValue = value;
		}

	}

	private final TableBinding<O, ?>[] bindings;
	private final M objectModel;

	/**
	 * Binds all model properties with this model's bindings
	 *
	 * @param aModel
	 */
	protected abstract void bindModel(M anObjectModel);

	protected abstract AbstractProperty getPropertyAt(M anObjectModel, C column);

	public ObjectControllerTableModel(final ListModel<O> listModel, final M objectModel,
			final Class<C> columnsEnumClass) {
		super(listModel, columnsEnumClass);
		this.objectModel = objectModel;
		bindings = new TableBinding[columnsEnumClass.getEnumConstants().length];
		bindModel(objectModel);

		for (final C column : columnsEnumClass.getEnumConstants()) {
			bindings[column.ordinal()].property = getPropertyAt(objectModel, column);
		}
	}

	protected <U> IComponentBinding<U> getColumnBinding(final C column) {
		final TableBinding<O, U> binding = new TableBinding<>();
		bindings[column.ordinal()] = binding;
		return binding;
	}

	@Override
	protected Object getValueAtColumn(final O object, final C column) {
		final TableBinding<O, ?> binding = bindings[column.ordinal()];
		objectModel.setCurrentObject(object);
		return binding.getDisplayValue(object);
	}

	@Override
	protected void setValueAtColumn(final O object, final C column, final Object value) {
		bindings[column.ordinal()].addChange(object, value);
	}

	public void commit() {
		final Set<O> changes = new HashSet<>();
		for (final TableBinding<O, ?> binding : bindings) {
			changes.addAll(binding.changes.keySet());
		}
		for (final O change : changes) {
			objectModel.setCurrentObject(change);
			try (IEdition<O> edition = model.startEditingValue(change)) {
				for (final TableBinding<?, ?> binding : bindings) {
					binding.commit(change);
				}
			}
		}
		for (final TableBinding<?, ?> binding : bindings) {
			binding.changes.clear();
		}

	}
}
