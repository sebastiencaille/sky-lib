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
 * ListView.hh
 *
 *  Created on: Apr 6, 2012
 *      Author: scaille
 */

#ifndef ORG_SKYMARSHALL_LISTVIEW_HH_
#define ORG_SKYMARSHALL_LISTVIEW_HH_

#include <string>
#include <vector>

namespace ch_skymarshall::gui {

using namespace std;
using namespace __gnu_cxx;

template<class _Tp> class list_model;

/**
 * @param <value_type>
 *
 * Listener over list model events.
 */
template<typename _Tp> class list_model_listener {
	using value_type = _Tp;
	using model_type = list_model<value_type>;
	using data_list_type = vector<value_type>;

public:

	class event {

	private:
		const model_type *m_source;

		data_list_type m_objects;

	public:
		explicit event(const model_type *_source) :
				m_source(_source) {
		}

		explicit event(const model_type *_source, value_type _object) :
				m_source(_source) {
			m_objects.push_back(_object);
		}

		explicit event(const model_type *_source, data_list_type &_objects) :
				m_source(_source), m_objects(_objects) {
		}

		const model_type* get_source() {
			return m_source;
		}

		data_list_type& get_objects() {
			return m_objects;
		}

		value_type get_object() {
			if (m_objects.size() > 1) {
				throw string("Event has more than one object");
			}
			return *m_objects.begin();
		}

	};

	virtual ~list_model_listener() = default;

	virtual void values_cleared(event &event) = 0;

	virtual void values_added(event &event) = 0;

	virtual void values_removed(event &event) = 0;

	virtual void edition_cancelled(event &event) = 0;

	virtual void editions_started(event &event) = 0;

	virtual void editions_stopping(event &event) = 0;

	virtual void editions_stopped(event &event) = 0;

};

template<class _Tp> class list_model_view {

protected:
	using value_type = _Tp;

public:

	using view_ptr = shared_ptr<list_model_view<value_type>>;

	/**
	 *
	 */
	using filter = function<bool(value_type const&)>;

	/**
	 *
	 */
	using comparator = function<int(value_type const&, value_type const&)>;

	/**
	 *
	 */
	class owner {

	public:
		virtual ~owner() = default;
		virtual view_ptr get_parent_view() = 0;

	};

	virtual ~list_model_view() = default;

	virtual bool accept(value_type const &_object) const = 0;

	// Compare
	virtual int compare(value_type const &_o1, value_type const &_o2) const = 0;

	virtual void attach(owner *_owner) {
	}

	virtual void detach(owner *_owner) {
	}

};

}

#endif /* ORG_SKYMARSHALL_LISTVIEW_HH_ */
