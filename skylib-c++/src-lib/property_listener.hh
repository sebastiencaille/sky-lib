
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
#include <iostream>

#include "utils.hh"
#include "types.hh"

namespace ch_skymarshall::gui {

using namespace std;

/**
 * Untyped reference to property_listener
 */
using property_listener_ref = void*;

class property_listener {
public:

	virtual ~property_listener() = default;

	virtual void fire(source_ptr _source, const string_view &_name,
			const void *_oldValue, const void *_newValue) = 0;

	virtual void before_change(source_ptr _source, property *_property) = 0;

	virtual void after_change(source_ptr _source, property *_property) = 0;

};

/**
 *  Container used to provide both instance and instance method so one can method on correct instance
 */
class property_listener_dispatcher: public property_listener {
public:
	using fire_function = function<void (source_ptr, const string_view&, const void*, const void*)>;
	using before_after_function = function<void (source_ptr, property*)>;
private:
	/** Keep reference as long as listener is valid */
	shared_ptr<void> m_owner;

	fire_function m_func_fire = nullptr;
	before_after_function m_func_before = nullptr;
	before_after_function m_func_after = nullptr;

public:

	static shared_ptr<property_listener_dispatcher> ofLazy(
			weak_ptr<property_listener_dispatcher> &_lazy,
			weak_ptr<void> const &_owner, fire_function const &_fireFunction) {
		return ch_skymarshall::utils::withLazy<property_listener_dispatcher>(
				_lazy, _owner,
				[_owner, _fireFunction] {
					return make_shared<property_listener_dispatcher>(_owner,
							_fireFunction);
				});
	}

	static shared_ptr<property_listener_dispatcher> ofLazy(
			weak_ptr<property_listener_dispatcher> &_lazy,
			weak_ptr<void> const &_owner,
			before_after_function const &_beforeFireFunction,
			before_after_function const &_afterFireFunction) {
		return ch_skymarshall::utils::withLazy<property_listener_dispatcher>(
				_lazy, _owner,
				[_owner, _beforeFireFunction, _afterFireFunction] {
					return make_shared<property_listener_dispatcher>(_owner,
							_beforeFireFunction, _afterFireFunction);
				});
	}

	explicit property_listener_dispatcher(weak_ptr<void> _owner,
			fire_function const &_fireFunction) :
			m_owner(_owner.lock()), m_func_fire(_fireFunction) {
	}

	explicit property_listener_dispatcher(weak_ptr<void> _owner,
			before_after_function const &_beforeFireFunction,
			before_after_function const &_afterFireFunction) :
			m_owner(_owner.lock()), m_func_before(_beforeFireFunction), m_func_after(
					_afterFireFunction) {
	}

	explicit property_listener_dispatcher(weak_ptr<void> _owner,
			property_listener_dispatcher const &_p) :
			m_owner(_owner.lock()), m_func_fire(_p.m_func_fire), m_func_before(
					_p.m_func_before), m_func_after(_p.m_func_after) {
	}

	void fire(source_ptr _source, const string_view &_name, const void *_oldValue,
			const void *_newValue) override {
		if (m_func_fire != nullptr) {
			m_func_fire(_source, _name, _oldValue, _newValue);
		}
	}

	void before_change(source_ptr _source, property *_property) override {
		if (m_func_before != nullptr) {
			m_func_before(_source, _property);
		}
	}

	void after_change(source_ptr _source, property *_property) override {
		if (m_func_after != nullptr) {
			m_func_after(_source, _property);
		}
	}

	~property_listener_dispatcher() override
	DESTR_WITH_LOG("~property_listener_dispatcher")

};
}

#endif /* PROPERTYLISTENER_HH_ */
