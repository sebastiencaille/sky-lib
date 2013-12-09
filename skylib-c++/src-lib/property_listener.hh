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

namespace org_skymarshall_util_hmi {

using namespace std;

/**
 * Untyped reference to property_listener
 */
typedef void* property_listener_ref;

class property;

class property_listener {
public:

	virtual ~property_listener() {
	}

	virtual void fire(const void* _source, const string& _name, const void* _oldValue, const void* _newValue) = 0;

	virtual void before_change(const void* _source, const property* _property) = 0;

	virtual void after_change(const void* _source, const property* _property) = 0;

};

/**
 *  Container used to provide both instance and instance method so one can method on correct instance
 */
template<class C> class property_listener_func_type:
		public property_listener {
public:
	typedef void (C::*fire_function)(const void*, const string&, const void*, const void*);
	typedef void (C::*before_after_function)(const void*, const property*);
private:
	C* m_obj;
	fire_function m_func_fire;
	before_after_function m_func_before;
	before_after_function m_func_after;

public:
	property_listener_func_type(C* _obj, fire_function _fireFunction) :
					m_obj(_obj),
					m_func_fire(_fireFunction),
					m_func_before(NULL),
					m_func_after(NULL) {
	}

	property_listener_func_type(C& _obj, fire_function _fireFunction) :
					m_obj(&_obj),
					m_func_fire(_fireFunction),
					m_func_before(NULL),
					m_func_after(NULL) {
	}

	property_listener_func_type(C* _obj, before_after_function _beforeFireFunction, before_after_function _afterFireFunction) :
					m_obj(_obj),
					m_func_fire(NULL),
					m_func_before(_beforeFireFunction),
					m_func_after(_afterFireFunction) {
	}

	property_listener_func_type(C& _obj, before_after_function _beforeFireFunction, before_after_function _afterFireFunction) :
					m_obj(&_obj),
					m_func_fire(NULL),
					m_func_before(_beforeFireFunction),
					m_func_after(_afterFireFunction) {
	}

	property_listener_func_type(property_listener_func_type<C>& _p) :
					m_obj(_p.m_obj),
					m_func_fire(_p.m_func_fire),
					m_func_before(_p.m_func_before),
					m_func_after(_p.m_func_after) {
	}

	void fire(const void* _source, const string& _name, const void* _oldValue, const void* _newValue) {
		if (m_func_fire != NULL) {
			(m_obj->*m_func_fire)(_source, _name, _oldValue, _newValue);
		}
	}

	void before_change(const void* _source, const property* _property) {
		if (m_func_before != NULL) {
			(m_obj->*m_func_before)(_source, _property);
		}
	}

	void after_change(const void* _source, const property* _property) {
		if (m_func_after != NULL) {
			(m_obj->*m_func_after)(_source, _property);
		}
	}

};

template<class C> property_listener_func_type<C>* property_listener_func(C*_obj,
		void (C::*_func)(const void*, const string&, const void*, const void*));

template<class C> property_listener_func_type<C>* property_listener_func(C& _obj,
		void (C::*_func)(const void*, const string&, const void*, const void*));

}

#endif /* PROPERTYLISTENER_HH_ */
