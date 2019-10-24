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
 * PropertyListener.hh
 *
 *  Created on: Apr 4, 2012
 *      Author: scaille
 */

#ifndef PROPERTYLISTENER_HH_
#define PROPERTYLISTENER_HH_

#include <string>
#include <functional>

#include "types.hh"

namespace org_skymarshall_util_hmi {

using namespace std;

/**
 * Untyped reference to property_listener
 */
typedef void* property_listener_ref;

class property_listener {
public:

	virtual ~property_listener() {
	}

	virtual void fire(source_ptr _source, const string& _name, const void* _oldValue, const void* _newValue) = 0;

	virtual void before_change(source_ptr _source, const property* _property) = 0;

	virtual void after_change(source_ptr _source, const property* _property) = 0;

};

/**
 *  Container used to provide both instance and instance method so one can method on correct instance
 */
class property_listener_dispatcher:
		public property_listener {
public:
	typedef std::function<void (source_ptr, const string&, const void*, const void*)> fire_function;
	typedef std::function<void (source_ptr, const property*)> before_after_function;
private:
	fire_function m_func_fire;
	before_after_function m_func_before;
	before_after_function m_func_after;

public:
	property_listener_dispatcher(fire_function _fireFunction) :
					m_func_fire(_fireFunction),
					m_func_before(NULL),
					m_func_after(NULL) {
	}

	property_listener_dispatcher(before_after_function _beforeFireFunction, before_after_function _afterFireFunction) :
					m_func_fire(NULL),
					m_func_before(_beforeFireFunction),
					m_func_after(_afterFireFunction) {
	}

	property_listener_dispatcher(property_listener_dispatcher& _p) :
					m_func_fire(_p.m_func_fire),
					m_func_before(_p.m_func_before),
					m_func_after(_p.m_func_after) {
	}

	void fire(source_ptr _source, const string& _name, const void* _oldValue, const void* _newValue) {
		if (m_func_fire != NULL) {
			m_func_fire(_source, _name, _oldValue, _newValue);
		}
	}

	void before_change(source_ptr _source, const property* _property) {
		if (m_func_before != NULL) {
			m_func_before(_source, _property);
		}
	}

	void after_change(source_ptr _source, const property* _property) {
		if (m_func_after != NULL) {
			m_func_after(_source, _property);
		}
	}

	~property_listener_dispatcher() = default;
};
}

#endif /* PROPERTYLISTENER_HH_ */
