/*
*Copyright (c) 2013 Sebastien Caille.
*All rights reserved.
*
*Redistribution and use in source and binary forms are permitted
*provided that the above copyright notice and this paragraph are
*duplicated in all such forms and that any documentation,
*advertising materials, and other materials related to such
*distribution and use acknowledge that the software was developed
*by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
*from this software without specific prior written permission.
*THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
*IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
*WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
*/
/*
 * DynamicList.hh
 *
 *  Created on: Apr 6, 2012
 *      Author: scaille
 */
#ifndef DYNAMICLIST_HH_
#define DYNAMICLIST_HH_

#include <vector>
#include <map>
#include <iostream>
#include <sstream>
#include <algorithm>
#include <tr1/memory>

#include <stdio.h>
#include <stdarg.h>

#include <property_manager.hh>
#include <typed_property.hh>
#include "list_model_interfaces.hh"

namespace org_skymarshall_util_hmi {

using namespace std;
using namespace __gnu_cxx;

/**
 * @param <_Tp>
 *
 * List Model with log(n) access.
 */
template<typename _Tp> class list_model {
private:

	typedef _Tp value_type;
	typedef list_model<value_type> model_type;

	typedef vector<value_type> value_list_type;
	typedef typename value_list_type::iterator value_list_iterator;
	typedef typename value_list_type::const_iterator value_list_citerator;

	typedef list_model_listener<value_type> model_listener_type;
	typedef typename list_model_listener<value_type>::event model_listener_event;

	typedef vector<model_listener_type*> model_listener_list_type;
	typedef typename model_listener_list_type::iterator model_listener_iterator;

	typedef list_model_view<value_type> view_type;
	typedef typename view_type::owner view_owner;
	typedef typename view_type::filter view_filter;
	typedef typename view_type::comparator view_comparator;

	/*
	 * A sorted and filtered view on the model
	 */
	class view_impl:
			public view_type {

		friend class list_model<value_type> ;

	private:
		view_filter* m_filter;
		view_comparator* m_comparator;
		view_type* m_parentView;

		view_impl(view_comparator* _comparator, view_filter* _filter) :
						m_filter(_filter),
						m_comparator(_comparator),
						m_parentView(NULL) {
		}

		view_impl(view_impl& _view) :
						m_filter(_view.m_filter),
						m_comparator(_view.m_comparator),
						m_parentView(_view.m_parentView) {
		}

	public:

		bool accept(const value_type _object) const {
			return m_filter == NULL || m_filter->accept(_object);
		}

		int compare(const value_type _o1, const value_type _o2) const {
			int compare;
			if (m_comparator == NULL && m_parentView == NULL) {
				throw string("You must either set a comparator or override this method");
			} else if (m_comparator != NULL) {
				compare = m_comparator->compare(_o1, _o2);
			} else {
				compare = 0;
			}
			if (compare == 0 && m_parentView != NULL) {
				return m_parentView->compare(_o1, _o2);
			}
			return compare;
		}

		void attach(view_owner* _owner) {
			m_parentView = _owner->get_parent_view();
		}

		void detach(view_owner* _owner) {
		}

		string str() const {
			stringstream ss;
			ss << "ListView:[comparator=" << m_comparator << ", filter=" << m_filter << ']';
			return ss.str();
		}

	};

public:

	static view_type* sorted(view_comparator* comparator) {
		return new view_impl(comparator, NULL);
	}

	static view_type* sorted_filtered(view_comparator* comparator, view_filter* filter) {
		return new view_impl(comparator, filter);
	}

	static view_type* filtered(view_filter* filter) {
		return new view_impl(NULL, filter);
	}

	static view_type* inherited() {
		return new view_impl(NULL, NULL);
	}

public:

	/**
	 * To provide str and equals for a given object
	 */
	class object_tunings {
	public:
		object_tunings() {
		}

		virtual ~object_tunings() {
			cout << "Deleting an object tuning" << endl;
		}

		virtual string str(const value_type _value) const {
			stringstream ss;
			ss << hex << &_value;
			return ss.str();
		}

		virtual bool equals(value_type _val1, value_type _val2) const {
			return _val1 == _val2;
		}
	};

	typedef std::tr1::shared_ptr<object_tunings> object_tuning_ptr;
	static object_tuning_ptr make_ptr(object_tunings* _tuning) {
		return object_tuning_ptr(_tuning);
	}

private:

	/**
	 * Value Comparator
	 */
	template<class _Compared> class view_comparator_func {

	private:
		const model_type * const m_obj;
		const view_type * const m_view;
		bool (model_type::* const m_func)(const view_type*, _Compared _o1, _Compared _o2) const;
	public:
		view_comparator_func(const model_type* const _obj,
				bool (model_type::*_func)(const view_type*, _Compared _o1, _Compared _o2) const) :
						m_obj(_obj),
						m_view(_obj->get_view()),
						m_func(_func) {
		}

		bool operator()(_Compared _o1, _Compared _o2) const {
			return (m_obj->*m_func)(m_view, _o1, _o2);
		}

	};

	/**
	 *
	 */
	class value_edition {

		typedef _Tp value_type;

		friend class list_model<value_type> ;

		model_type* m_model;
		value_type m_value;
		bool m_accepted;
		int m_oldIndex;
		int m_newIndex;

	public:
		value_edition() :
						m_model(NULL),
						m_value(),
						m_accepted(false),
						m_oldIndex(-1),
						m_newIndex(-1) {

		}

		value_edition(model_type* _model, value_type _value, int _oldIndex) :
						m_model(_model),
						m_value(_value),
						m_oldIndex(_oldIndex),
						m_newIndex(-1) {
			m_accepted = m_model->get_view()->accept(m_value);
		}

		void compute_new_index() const {
			m_newIndex = m_model->compute_insertion_point(m_value) - m_model->m_data.begin();
			//cout << "insertion point of " << m_model->m_tunings->str(m_value) << ":  " << m_newIndex << endl;
		}

		bool accepted() const {
			return m_accepted;
		}

		string str() const {
			stringstream ss;
			ss << "index=" << m_oldIndex << "->" << m_newIndex;
			return ss;
		}

		void update_accepted() {
			m_accepted = m_model->get_view()->accept(m_value);
		}
	};

	typedef vector<value_edition*> edition_list;
	typedef typename edition_list::iterator edition_list_iterator;

private:

	model_listener_list_type m_listeners;

	/**
	 * Current edition
	 */
	value_edition* m_objectEdition;

	property_manager m_propertyManager;

	value_list_type m_data;

	model_type* m_source;

	typed_property<list_model_view<value_type>*> m_viewProperty;

	/**
	 * Local listeners
	 */
	class private_listeners_impl:
			public list_model_view<value_type>::owner,
			public property_listener,
			public list_model_listener<value_type> {

		model_type* m_model;

		friend class list_model;

		private_listeners_impl(model_type* _model) :
						m_model(_model) {
		}

		list_model_view<value_type>* get_parent_view() {
			if (m_model->m_source != NULL) {
				return m_model->m_source->get_view();
			}
			return NULL;
		}

		virtual void fire(const void* _source, const string& _name, const void* _oldValue, const void* _newValue) {
			m_model->view_updated();
		}

		void values_cleared(model_listener_event& event) {
			m_model->clear();
		}

		void values_added(model_listener_event& event) {
			if (m_model->m_data.size() / event.get_objects().size() < 2) {
				value_list_type& values = event.get_objects();
				value_list_iterator iter;
				for (iter = values.begin(); iter != values.end(); iter++) {
					m_model->insert(*iter);
				}
			} else {
				m_model->insert(event.get_objects());
			}
		}

		void values_removed(model_listener_event& event) {
			value_list_type& values = event.get_objects();
			value_list_iterator iter;
			for (iter = values.begin(); iter != values.end(); iter++) {
				m_model->remove(*iter, NULL);
			}
		}

		void edition_cancelled(model_listener_event& event) {
			m_model->m_objectEdition = NULL;
		}

		void editions_started(model_listener_event& event) {
			m_model->start_editing_value(event.get_object());
		}

		void editions_stopping(model_listener_event& event) {
		}

		void editions_stopped(model_listener_event& event) {
			m_model->stop_editing_value();
		}

		void before_change(const void* _source, const property* _property) {
			// nope
		}

		void after_change(const void* _source, const property* _property) {
			// nope
		}

	} m_privateListenersImpl;

	object_tuning_ptr m_tunings;

	void view_updated(const void* _source, const string& _name, const void* _oldValue, const void* _newValue) {
		fire_view_updated();
	}

	bool compare_data(const view_type* _view, const value_type _o1, const value_type _o2) const {
		return _view->compare(_o1, _o2) < 0;
	}

	value_list_iterator compute_insertion_point(const value_type _value) {
		return lower_bound(m_data.begin(), m_data.end(), _value,
				view_comparator_func<value_type>(this, &list_model::compare_data));
	}


public:

	list_model(list_model_view<value_type>* _view) :
					m_objectEdition(NULL),
					m_source(NULL),
					m_viewProperty("View", m_propertyManager, NULL),
					m_privateListenersImpl(this) {
		m_tunings = make_ptr(new object_tunings());
		if (_view == NULL) {
			throw string("View must not be NULL");
		}
		set_view(_view);
	}

	list_model(model_type& _source) :
					m_objectEdition(NULL),
					m_source(&_source),
					m_viewProperty("View", m_propertyManager, NULL),
					m_privateListenersImpl(this) {
		m_tunings = _source.m_tunings;
		attach_to_source();
		set_view(inherited());
		set_tunings(_source.m_tunings);
	}

	list_model(model_type& _source, list_model_view<value_type>& _view) :
					m_objectEdition(NULL),
					m_source(_source),
					m_viewProperty("View", m_propertyManager, NULL),
					m_privateListenersImpl(this) {
		m_tunings = _source.m_tunings;
		attach_to_source();
		set_view(_view);
	}

	virtual ~list_model() {
	}

	void set_tunings(object_tuning_ptr _tunings) {
		m_tunings = _tunings;
	}

	virtual bool verbose() const {
		return false;
	}

	void attach_to_source() {
		m_source->add_listener(&m_privateListenersImpl);
		m_source->m_viewProperty.add_listener(&m_privateListenersImpl);
	}

	void dispose() {
		if (m_source != NULL) {
			m_source->remove_listener(&m_privateListenersImpl);
			m_source->m_viewProperty.remove_listener(&m_privateListenersImpl);
		}
	}

	void add_listener(model_listener_type* listener) {
		m_listeners.push_back(listener);
	}

	void remove_listener(model_listener_type* listener) {
		m_listeners.remove(listener);
	}

	void fire_values_cleared(value_list_type& _cleared) {
		model_listener_event event(this, _cleared);
		model_listener_iterator iter;
		for (iter = m_listeners.begin(); iter != m_listeners.end(); iter++) {
			(*iter)->values_cleared(event);
		}
	}

	void fire_values_added(value_list_type& _added) {
		model_listener_event event(this, _added);
		model_listener_iterator iter;
		for (iter = m_listeners.begin(); iter != m_listeners.end(); iter++) {
			(*iter)->values_added(event);
		}
	}

	void fire_value_added(value_type _object) {
		model_listener_event event(this, _object);
		model_listener_iterator iter;
		for (iter = m_listeners.begin(); iter != m_listeners.end(); iter++) {
			(*iter)->values_added(event);
		}
	}

	void fire_value_removed(value_type _object) {
		model_listener_event event(this, _object);
		model_listener_iterator iter;
		for (iter = m_listeners.begin(); iter != m_listeners.end(); iter++) {
			(*iter)->values_removed(event);
		}
	}

	void fire_edition_cancelled(value_type _value) {
		model_listener_event event(this, _value);
		model_listener_iterator iter;
		for (iter = m_listeners.begin(); iter != m_listeners.end(); iter++) {
			(*iter)->edition_cancelled(event);
		}
	}

	void fire_edition_started(value_type _value) {
		model_listener_event event(this, _value);
		model_listener_iterator iter;
		for (iter = m_listeners.begin(); iter != m_listeners.end(); iter++) {
			(*iter)->editions_started(event);
		}
	}

	void fire_edition_stopping() {
		if (m_objectEdition == NULL) {
			return;
		}
		model_listener_event event(this, m_objectEdition->m_value);
		model_listener_iterator listenerIter;
		for (listenerIter = m_listeners.begin(); listenerIter != m_listeners.end(); listenerIter++) {
			(*listenerIter)->editions_stopping(event);
		}
	}

	void fire_edition_stopped() {
		if (m_objectEdition == NULL) {
			return;
		}
		model_listener_event event(this, m_objectEdition->m_value);
		model_listener_iterator listenerIter;
		for (listenerIter = m_listeners.begin(); listenerIter != m_listeners.end(); listenerIter++) {
			(*listenerIter)->editions_stopped(event);
		}
	}

	void fire_view_updated() {
		m_viewProperty.force_changed(this);
	}

	list_model_view<value_type>* get_view() const {
		return m_viewProperty.get();
	}

	void view_updated() {
		rebuild_model();
		fire_view_updated();
	}

	void set_view(list_model_view<value_type>* _view) {
		if (m_viewProperty.get() != NULL) {
			m_viewProperty.get()->detach(&m_privateListenersImpl);
		}
		if (_view != NULL) {
			m_viewProperty.set(this, _view);
		} else {
			m_viewProperty.set(this, inherited());
		}
		m_viewProperty.get()->attach(&m_privateListenersImpl);
		view_updated();
	}

	void rebuild_model() {
		check_no_edition();
		value_list_type newData;
		if (m_source != NULL) {
			for (int i = 0; i < m_source->get_size(); i++) {
				newData.push_back((*m_source)[i]);
			}
		} else {
			for (unsigned int i = 0; i < m_data.size(); i++) {
				newData.push_back(m_data[i]);
			}
		}
		set_values(newData);
	}

	value_type get_element_at(int index) const {
		return m_data[index];
	}

	int get_size() const {
		return m_data.size();
	}

	value_list_iterator& iterator() {
		return m_data.iterator();
	}

	void set_values(value_list_type& _newData) {
		clear();
		insert(_newData);
	}

	void insert(value_list_type& _newData) {
		check_no_edition();
		value_list_type addedData;
		value_list_iterator iter;
		for (iter = _newData.begin(); iter != _newData.end(); iter++) {
			value_type value = (*iter);
			if (m_viewProperty.get()->accept(value)) {
				m_data.push_back(value);
				addedData.push_back(value);
			}
		}
		sort(m_data.begin(), m_data.end(), view_comparator_func<const value_type>(this, &model_type::compare_data));
		if (addedData.size() > 0) {
			fire_values_added(addedData);
		}
	}

	void clear() {
		check_no_edition();
		value_list_type removed(m_data);
		m_data.clear();
		fire_values_cleared(removed);
	}

protected:

	int remove_from_model(value_type _sample, value_type* _removed) {
		check_no_edition();
		const int row = row_of(_sample);
		if (row >= 0) {
			const value_list_iterator iter = m_data.begin() + row;
			if (_removed != NULL) {
				*_removed = *iter;
			}
			m_data.erase(iter);
			fire_value_removed(*iter);
		}
		return row;
	}

public:

	int insert(value_type _value) {
		check_no_edition();
		if (m_viewProperty.get()->accept(_value)) {
			const value_list_iterator insertPoint = compute_insertion_point(_value);
			m_data.insert(insertPoint, _value);
			fire_value_added(_value);
			return insertPoint - m_data.begin();
		}
		return -1;
	}

	int remove(value_type _sample, value_type* _removed) {
		return remove_from_model(_sample, _removed);
	}

	int remove(int _row, value_type* _removed) {
		value_type value = this[_row];
		remove_from_model(value, _removed);
		return value;
	}

	value_type get_edited_value() const {
		if (m_objectEdition == NULL) {
			return NULL;
		}
		return m_objectEdition->m_value;
	}

	void start_editing_value(value_type _value) {
		const int oldIndex = row_of(_value);
		m_objectEdition = new value_edition(this, _value, oldIndex);
		fire_edition_started(_value);
	}

private:

	bool compare_edition_index(const view_type* const _view, const value_edition* const _o1,
			const value_edition* const _o2) const {
		return _o2->m_oldIndex > _o1->m_oldIndex;
	}

	bool compare_edition_value(const view_type* const _view, const value_edition* const _o1,
			const value_edition* const _o2) const {
		return _view->compare(_o2->m_value, _o1->m_value);
	}

	void check_no_edition() const {
		if (m_objectEdition != NULL) {
			stringstream ss;
			ss << "Edition already in progress: " << m_tunings->str(m_objectEdition->m_value);
			throw new string(ss.str());
		}
	}

public:

	void stop_editing_value() {
		fire_edition_stopping();
		if (m_objectEdition == NULL) {
			return;
		}

		if (m_objectEdition->m_oldIndex >= 0) {
			//cout << "Removing " << m_tunings->str(m_objectEdition->m_value) << endl;
			m_data.erase(m_data.begin() + m_objectEdition->m_oldIndex);
		}
		const value_list_iterator insertionPoint = compute_insertion_point(m_objectEdition->m_value);

		m_objectEdition->update_accepted();
		if (!m_objectEdition->accepted() && m_objectEdition->m_oldIndex < 0) {
			fire_edition_cancelled(m_objectEdition->m_value);
		} else if (!m_objectEdition->accepted()) {
			fire_edition_cancelled(m_objectEdition->m_value);
			//fire_interval_removed(this, m_objectEdition->m_oldIndex, m_objectEdition->m_oldIndex);
			fire_value_removed(m_objectEdition->m_value);
		} else if (m_objectEdition->m_oldIndex < 0) {
			fire_edition_cancelled(m_objectEdition->m_value);
			m_data.insert(insertionPoint, m_objectEdition->m_value);
			//fire_interval_added(this, newIndex, newIndex);
			fire_value_added(m_objectEdition->m_value);
		} else {
			//cout << "Putting back " << m_tunings->str(m_objectEdition->m_value) << " at " << (insertionPoint - m_data.begin()) << endl;
			m_data.insert(insertionPoint, m_objectEdition->m_value);
			//fire_contents_changed(this, newIndex, newIndex);
			fire_edition_stopped();
		}
		m_objectEdition = NULL;
	}
public:
	int row_of(value_type value) const {
		const value_list_citerator& begin = m_data.begin();
		value_list_citerator found = lower_bound(begin, m_data.end(), value,
				view_comparator_func<const value_type>(this, &list_model::compare_data));
		if (found == m_data.end()) {
			return -1;
		}
		int index = found - begin;

		//cout << "Found at row " << index << ": " << m_tunings->str(*found) << endl;

		if (m_tunings->equals(*found, value)) {
			return index;
		}
		unsigned int max = index + 1;
		while (max < m_data.size() && m_viewProperty.get()->compare(value, m_data[max]) == 0) {
			if (m_tunings->equals(m_data[max], value)) {
				return max;
			}
			max++;
		}
		return -index;
	}

	value_type operator[](int row) const {
		return m_data[row];
	}

	string & str() const {
		stringstream ss;
		ss << "Model[" << hex << this << ", " << m_viewProperty.get_value().str() << ']';
		return ss;
	}

	int find(value_type _sample, value_type* _found) const {
		const int row = row_of(_sample);
		if (row >= 0) {
			*_found = m_data[row];
		}
		return row;
	}

	int find_for_edition(value_type _sample, value_type* _found) {
		int row = find(_sample, _found);
		if (row >= 0) {
			start_editing_value(*_found);
		}
		return row;
	}

	int find_or_create(value_type _sample) {
		value_type* found;
		int row = find(_sample, found);
		if (row < 0) {
			insert(_sample);
			*found = _sample;
		}
		return *found;
	}

	value_type find_or_create_for_edition(value_type _sample) {
		value_type found = find_or_create(_sample);
		start_editing_value(found);
		return found;
	}

	property_listener & get_view_updated_listener() const {
		return m_privateListenersImpl;
	}

	string str(const value_type _object) const {
		return m_tunings.get()->str(_object);
	}

private:

};

}
#endif /* DYNAMICLIST_HH_ */
