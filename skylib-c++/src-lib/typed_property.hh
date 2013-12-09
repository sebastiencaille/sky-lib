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
 * Properties.hh
 *
 *  Created on: Feb 19, 2012
 *      Author: scaille
 */

#ifndef TYPEDPROPERTY_HH_
#define TYPEDPROPERTY_HH_

#include <string>
#include "property.hh"

namespace org_skymarshall_util_hmi {

using namespace std;

/**
 * Property with type
 */
template<typename _Pt> class typed_property:
		public property {
private:
	typedef _Pt value_type;
	typedef const _Pt value_const_type;
	value_type m_value;

public:

	typed_property(const string& _name, property_manager& _manager, value_type _defaultValue) :
					property(_name, _manager),
					m_value(_defaultValue) {
	}

	typed_property(const char* _name, property_manager& _manager, value_type _defaultValue) :
					property(_name, _manager),
					m_value(_defaultValue) {
	}

	~typed_property() {

	}

	value_type get() {
		return m_value;
	}

	value_const_type get() const {
		return m_value;
	}

	void set(const void * _caller, value_type const _newValue) {
		if (m_value != _newValue) {
			m_manager.fire_before_property_changed(_caller, this);
			value_type oldValue = m_value;
			m_value = _newValue;
			m_manager.fire_property_changed(_caller, m_name, (const void*) &oldValue, (const void*) &_newValue);
			m_manager.fire_after_property_changed(_caller, this);
		}
	}

	void force_changed(const void* _caller) {
		m_manager.fire_property_changed(_caller, m_name, NULL, m_value);
	}

};

}

#endif /* TYPEDPROPERTY_HH_ */
