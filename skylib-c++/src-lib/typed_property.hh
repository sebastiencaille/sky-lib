
/*
 * Properties.hh
 *
 *  Created on: Feb 19, 2012
 *      Author: scaille
 */

#ifndef TYPEDPROPERTY_HH_
#define TYPEDPROPERTY_HH_

#include <memory>

#include <string>
#include "property.hh"

namespace ch_skymarshall::gui {

using namespace std;

/**
 * Property with type
 */
template<typename _Pt> class typed_property: public property {

public:
	using value_type = _Pt;

private:
	using value_const_type = const _Pt;
	value_type m_value;

public:

	typed_property(const string_view &_name, property_manager &_manager,
			value_type _defaultValue) :
			property(_name, _manager), m_value(_defaultValue) {
	}

	typed_property(const char *_name, property_manager &_manager,
			value_type _defaultValue) :
			property(_name, _manager), m_value(_defaultValue) {
	}

	~typed_property() override = default;

	value_type get() const {
		return m_value;
	}

	void set(source_ptr _source, value_type const _newValue) {
		if (m_value == _newValue) {
			return;
		}
		m_manager.fire_before_property_changed(_source, this);
		value_type oldValue = m_value;
		m_value = _newValue;
		m_manager.fire_property_changed(_source, m_name,
				(const void*) &oldValue, (const void*) &_newValue);
		m_manager.fire_after_property_changed(_source, this);
	}

	void attach() override {
		force_changed(this);
	}

	void force_changed(source_ptr _source) {
		m_manager.fire_property_changed(_source, m_name, NULL,
				(const void*) &m_value);
	}

};

template<typename _Pt> class typed_property_shared_ptr: public typed_property<
		shared_ptr<_Pt>> {

public:

	typed_property_shared_ptr(const string &_name, property_manager &_manager,
			shared_ptr<_Pt> _defaultValue) :
			typed_property<shared_ptr<_Pt>>(_name, _manager, _defaultValue) {
	}

	typed_property_shared_ptr(const char *_name, property_manager &_manager,
			shared_ptr<_Pt> _defaultValue) :
			typed_property<shared_ptr<_Pt>>(_name, _manager, _defaultValue) {
	}
};

}

#endif /* TYPEDPROPERTY_HH_ */
