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
/*
/*
 * property_group.hh
 *
 *  Created on: May 5, 2013
 *      Author: scaille
 */

#ifndef PROPERTY_GROUP_HH_
#define PROPERTY_GROUP_HH_

#include <vector>
#include <set>

#include <property.hh>
#include <property_listener.hh>

namespace org_skymarshall_util_hmi {

using namespace std;

class property;

enum property_group_actions {
	BEFORE_FIRE, AFTER_FIRE
} property_group_actions_t;

class action {
public:
	virtual ~action() {
	}

	virtual void apply(property_group_actions _action, const property* _property) = 0;

};

template<class _T> class action_func_type:
		public action {
public:
	typedef void (_T::*action_function)(property_group_actions _action, const property* _property);

private:
	action_function const m_action;
	_T* const m_object;

public:
	action_func_type(_T* _object, action_function _action) :
					m_object(_object),
					m_action(_action) {
	}

	void apply(property_group_actions _action, const property* _property) {
		(m_object->*m_action)(_action, _property);
	}

};

class property_group {

public:

private:

	typedef set<property*> properties_set;
	typedef properties_set::iterator properties_set_iterator;

	vector<action*> m_actions;

	properties_set m_properties;

	int m_callCount;

	class impl:
			public property_listener {

		friend class property_group;

		property_group* m_group;

		impl(property_group* _group) :
						m_group(_group) {
		}

		void fire(const void* _source, const string& _name, const void* _oldValue, const void* _newValue) {
		}

		void before_change(const void* _caller, const property* _property) {
			if (m_group->m_callCount > 0) {
				return;
			}
			m_group->m_callCount++;
			for (int i = 0; i < m_group->m_actions.size(); i++) {
				m_group->m_actions[i]->apply(BEFORE_FIRE, _property);
			}
		}

		void after_change(const void* _caller, const property* _property) {
			m_group->m_callCount--;
			if (m_group->m_callCount != 0) {
				return;
			}
			for (int i = 0; i < m_group->m_actions.size(); i++) {
				m_group->m_actions[i]->apply(AFTER_FIRE, _property);
			}
		}

	};

	impl m_impl;

	property_listener_func_type<impl>* m_listener;


public:

	property_group() :
					m_impl(this),
					m_listener(
							new property_listener_func_type<impl>(&m_impl, &impl::before_change, &impl::after_change)),
					m_callCount(0) {
	}

	~property_group() {

		properties_set_iterator properties_iter = m_properties.begin();
		for (; properties_iter != m_properties.end(); properties_iter++) {
			(*properties_iter)->remove_listener(m_listener);
		}
		delete m_listener;
		for (int i = 0; i < m_actions.size(); i++) {
			delete m_actions[i];
		}
	}

	void add_property(property& _property) {
		m_properties.insert(&_property);
		_property.add_listener(m_listener);
	}

	void add_action(action* _action) {
		m_actions.push_back(_action);
	}

};

}

#endif /* PROPERTY_GROUP_HH_ */
