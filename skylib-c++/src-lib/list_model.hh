
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
#include <tr1/memory>
#include <functional>

#include <stdio.h>
#include <stdarg.h>

#include "property_manager.hh"
#include "typed_property.hh"
#include "list_model_interfaces.hh"
#include "utils.hh"

namespace ch_skymarshall::gui {

using namespace ch_skymarshall::utils;
using namespace std;
using namespace __gnu_cxx;
using namespace std::placeholders;

/**
 * @param <_Tp>
 *
 * List Model with log(n) access.
 */
template<typename _Tp> class list_model {

public:
	using view_type = list_model_view<_Tp>;
	using view_ptr = typename view_type::view_ptr;

private:

	using value_type = _Tp;
	using model_type = list_model<value_type>;

	using value_list_type = vector<value_type>;
	using value_list_iterator = typename value_list_type::iterator;
	using value_list_citerator = typename value_list_type::const_iterator;

	using model_listener_type = list_model_listener<value_type>;
	using model_listener_event = typename list_model_listener<value_type>::event;

	using model_listener_list_type = vector<model_listener_type*>;

	using view_owner = typename view_type::owner;
	using view_filter = typename view_type::filter;
	using view_comparator = typename view_type::comparator;

	/*
	 * A sorted and filtered view on the model
	 */
	class view_impl: public list_model_view<_Tp> {

		friend class list_model<_Tp> ;

		view_filter m_filter;
		view_comparator m_comparator;
		view_ptr m_parentView;

	public:

		view_impl(view_comparator _comparator, view_filter _filter) :
				m_filter(_filter), m_comparator(_comparator) {
		}
		~view_impl() = default;

		bool accept(const value_type &_object) const final {
			return m_filter == NULL || m_filter(_object);
		}

		int compare(const value_type &_o1, const value_type &_o2) const final {
			int compare;
			if (m_comparator == nullptr && m_parentView == nullptr) {
				throw gui_exception(
						"You must either set a comparator or override this method");
			} else if (m_comparator != NULL) {
				compare = m_comparator(_o1, _o2);
			} else {
				compare = 0;
			}
			if (compare == 0 && m_parentView != nullptr) {
				return m_parentView->compare(_o1, _o2);
			}
			return compare;
		}

		void attach(view_owner *_owner) {
			m_parentView = _owner->get_parent_view();
		}

		void detach(view_owner *_owner) final {
			// nope;
		}

		string str() const {
			stringstream ss;
			ss << "ListView:[comparator=" << m_comparator << ", filter="
					<< m_filter << ']';
			return ss.str();
		}

	};

public:

	static view_ptr sorted(view_comparator comparator) {
		return make_shared<view_impl>(comparator, nullptr);
	}

	static view_ptr sorted_filtered(view_comparator comparator,
			view_filter *filter) {
		return make_shared<view_impl>(comparator, filter);
	}

	static view_ptr filtered(view_filter filter) {
		return make_shared<view_impl>(nullptr, filter);
	}

	static view_ptr inherited() {
		return make_shared<view_impl>(nullptr, nullptr);
	}

	/**
	 * To provide str and equals for a given object
	 */
	class object_tunings {
	public:
		object_tunings() = default;

		virtual ~object_tunings() = default;

		virtual string str(value_type const &_value) const {
			stringstream ss;
			ss << hex << &_value;
			return ss.str();
		}

		virtual bool equals(value_type const &_val1,
				value_type const &_val2) const {
			return _val1 == _val2;
		}
	};

	using object_tuning_ptr = std::tr1::shared_ptr<object_tunings>;
	static object_tuning_ptr make_ptr(object_tunings *_tuning) {
		return object_tuning_ptr(_tuning);
	}

	/**
	 *
	 */
	class value_edition {

		using value_type = _Tp;

		friend class list_model<value_type> ;

		model_type *m_model = NULL;
		value_type m_value;
		bool m_accepted = false;
		int m_oldIndex = -1;
		int m_newIndex = -1;

	public:
		value_edition() :
				m_value() {
		}

		value_edition(model_type *_model, value_type _value, int _oldIndex) :
				m_model(_model), m_value(_value), m_oldIndex(_oldIndex) {
			m_accepted = m_model->get_view()->accept(m_value);
		}

		void compute_new_index() const {
			m_newIndex = m_model->compute_insertion_point(m_value)
					- m_model->m_data.begin();
			//cout << "insertion point of " << m_model->m_tunings->str(m_value) << ":  " << m_newIndex << endl;
		}

		bool accepted() const {
			return m_accepted;
		}

		string str() const {
			stringstream ss;
			ss << "index=" << m_oldIndex << "->" << m_newIndex;
			return ss.str();
		}

		void update_accepted() {
			m_accepted = m_model->get_view()->accept(m_value);
		}
	};

private:

	model_listener_list_type m_listeners;

	/**
	 * Current edition
	 */
	unique_ptr<value_edition> m_objectEdition;

	property_manager m_propertyManager;

	value_list_type m_data;

	shared_ptr<model_type> m_source;

	typed_property_shared_ptr<view_type> m_viewProperty;

	/**
	 * Local listeners
	 */
	class private_listeners_impl: public list_model_view<value_type>::owner,
			public property_listener,
			public list_model_listener<value_type> {

		model_type *m_model;

		friend class list_model;

		explicit private_listeners_impl(model_type *_model) :
				m_model(_model) {
		}

		view_ptr get_parent_view() {
			if (m_model->m_source != nullptr) {
				return m_model->m_source->get_view();
			}
			return view_ptr();
		}

		void values_cleared(model_listener_event &event) {
			m_model->clear();
		}

		void values_added(model_listener_event &event) {
			if (m_model->m_data.size() / event.get_objects().size() < 2) {
				for (value_type &value : event.get_objects()) {
					m_model->insert(value);
				}
			} else {
				m_model->insert(event.get_objects());
			}
		}

		void values_removed(model_listener_event &event) {
			for (value_type &value : event.get_objects()) {
				m_model->remove(value, NULL);
			}
		}

		void edition_cancelled(model_listener_event &event) {
			m_model->m_objectEdition = unique_ptr<value_edition>();
		}

		void editions_started(model_listener_event &event) {
			m_model->start_editing_value(event.get_object());
		}

		void editions_stopping(model_listener_event &event) final {
			// nope
		}

		void editions_stopped(model_listener_event &event) {
			m_model->stop_editing_value();
		}

		void fire(source_ptr _source, const string_view &_name,
				const void *_oldValue, const void *_newValue) final {
			m_model->view_updated();
		}

		void before_change(source_ptr _source, property *_property) final {
			// nope
		}

		void after_change(source_ptr _source, property *_property) final {
			// nope
		}

	};

	private_listeners_impl m_privateListenersImpl;

	object_tuning_ptr m_tunings;

	void view_updated(const void *_source, const string_view &_name,
			const void *_oldValue, const void *_newValue) {
		fire_view_updated();
	}

	bool compare_data(const value_type _o1, const value_type _o2) const {
		return m_viewProperty.get()->compare(_o1, _o2) < 0;
	}

	value_list_iterator compute_insertion_point(const value_type _value) {
		return lower_bound(m_data.begin(), m_data.end(), _value,
				std::bind(&list_model::compare_data, this, _1, _2));
	}

public:

	explicit list_model(view_ptr _view) :
			m_viewProperty("View", m_propertyManager, view_ptr()), m_privateListenersImpl(
					this) {
		m_tunings = make_ptr(new object_tunings());
		if (_view == NULL) {
			throw gui_exception("View must not be NULL");
		}
		set_view(_view);
	}

	explicit list_model(shared_ptr<model_type> _source) :
			m_source(_source), m_viewProperty("View", m_propertyManager, NULL), m_privateListenersImpl(
					this), m_tunings(_source.m_tunings) {
		attach_to_source();
		set_view(inherited());
		set_tunings(_source.m_tunings);
	}

	list_model(shared_ptr<model_type> _source, view_ptr _view) :
			m_source(_source), m_viewProperty("View", m_propertyManager, NULL), m_privateListenersImpl(
					this), m_tunings(_source.m_tunings) {
		attach_to_source();
		set_view(_view);
	}

	virtual ~list_model() = default;

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

	void add_listener(model_listener_type *listener) {
		m_listeners.push_back(listener);
	}

	void remove_listener(model_listener_type *listener) {
		m_listeners.remove(listener);
	}

	void fire_values_cleared(value_list_type &_cleared) {
		model_listener_event event(this, _cleared);
		for (model_listener_type *listener : m_listeners) {
			listener->values_cleared(event);
		}
	}

	void fire_values_added(value_list_type &_added) {
		model_listener_event event(this, _added);
		for (model_listener_type *listener : m_listeners) {
			listener->values_added(event);
		}
	}

	void fire_value_added(value_type _object) {
		model_listener_event event(this, _object);
		for (model_listener_type *listener : m_listeners) {
			listener->values_added(event);
		}
	}

	void fire_value_removed(value_type _object) {
		model_listener_event event(this, _object);
		for (model_listener_type *listener : m_listeners) {
			listener->values_removed(event);
		}
	}

	void fire_edition_cancelled(value_type _value) {
		model_listener_event event(this, _value);
		for (model_listener_type *listener : m_listeners) {
			listener->edition_cancelled(event);
		}
	}

	void fire_edition_started(value_type _value) {
		model_listener_event event(this, _value);
		for (model_listener_type *listener : m_listeners) {
			listener->editions_started(event);
		}
	}

	void fire_edition_stopping() {
		if (m_objectEdition == NULL) {
			return;
		}
		model_listener_event event(this, m_objectEdition->m_value);
		for (model_listener_type *listener : m_listeners) {
			listener->editions_stopping(event);
		}
	}

	void fire_edition_stopped() {
		if (m_objectEdition == NULL) {
			return;
		}
		model_listener_event event(this, m_objectEdition->m_value);
		for (model_listener_type *listener : m_listeners) {
			listener->editions_stopped(event);
		}
	}

	void fire_view_updated() {
		m_viewProperty.force_changed(this);
	}

	view_ptr get_view() const {
		return m_viewProperty.get();
	}

	void view_updated() {
		rebuild_model();
		fire_view_updated();
	}

	void set_view(view_ptr _view) {
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

	void set_values(value_list_type &_newData) {
		clear();
		insert(_newData);
	}

	void insert(value_list_type &_newData) {
		check_no_edition();
		value_list_type addedData;
		for (value_type value : _newData) {
			if (m_viewProperty.get()->accept(value)) {
				m_data.push_back(value);
				addedData.push_back(value);
			}
		}
		sort(m_data.begin(), m_data.end(),
				std::bind(&list_model::compare_data, this, _1, _2));
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

	int remove_from_model(value_type _sample, value_type *_removed) {
		check_no_edition();
		const int row = row_of(_sample);
		if (row >= 0) {
			auto iter = m_data.begin() + row;
			if (_removed != nullptr) {
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
			auto insertPoint = compute_insertion_point(_value);
			m_data.insert(insertPoint, _value);
			fire_value_added(_value);
			return insertPoint - m_data.begin();
		}
		return -1;
	}

	int remove(value_type _sample, value_type *_removed) {
		return remove_from_model(_sample, _removed);
	}

	int remove_at_index(int _row, value_type *_removed) {
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
		m_objectEdition = make_unique<value_edition>(this, _value, oldIndex);
		fire_edition_started(_value);
	}

private:

	bool compare_edition_index(const view_type *const _view,
			const value_edition *const _o1,
			const value_edition *const _o2) const {
		return _o2->m_oldIndex > _o1->m_oldIndex;
	}

	bool compare_edition_value(const view_type *const _view,
			const value_edition *const _o1,
			const value_edition *const _o2) const {
		return _view->compare(_o2->m_value, _o1->m_value);
	}

	void check_no_edition() const {
		if (m_objectEdition != NULL) {
			stringstream ss;
			ss << "Edition already in progress: "
					<< m_tunings->str(m_objectEdition->m_value);
			throw gui_exception(ss.str());
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
		auto insertionPoint = compute_insertion_point(m_objectEdition->m_value);

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

	int row_of(value_type value) const {
		auto begin = m_data.begin();
		auto found = lower_bound(begin, m_data.end(), value,
				std::bind(&list_model::compare_data, this, _1, _2));
		if (found == m_data.end()) {
			return -1;
		}
		int index = found - begin;

		//cout << "Found at row " << index << ": " << m_tunings->str(*found) << endl;

		if (m_tunings->equals(*found, value)) {
			return index;
		}
		unsigned int max = index + 1;
		while (max < m_data.size()
				&& m_viewProperty.get()->compare(value, m_data[max]) == 0) {
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

	const string& str() const {
		stringstream ss;
		ss << "Model[" << hex << this << ", "
				<< m_viewProperty.get_value().str() << ']';
		return ss.str();
	}

	int find(value_type _sample, value_type *_found) const {
		const int row = row_of(_sample);
		if (row >= 0) {
			*_found = m_data[row];
		}
		return row;
	}

	int find_for_edition(value_type _sample, value_type *_found) {
		int row = find(_sample, _found);
		if (row >= 0) {
			start_editing_value(*_found);
		}
		return row;
	}

	int find_or_create(value_type _sample) {
		value_type *found;

		if (find(_sample, found) < 0) {
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

	property_listener& get_view_updated_listener() const {
		return m_privateListenersImpl;
	}

	string str(const value_type _object) const {
		return m_tunings.get()->str(_object);
	}

};
}

#endif /* DYNAMICLIST_HH_ */
