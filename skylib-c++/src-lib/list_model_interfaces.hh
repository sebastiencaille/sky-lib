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

namespace org_skymarshall_util_hmi {

using namespace std;
using namespace __gnu_cxx;


template<class _Tp> class list_model;

/**
 * @param <value_type>
 *
 * Listener over list model events.
 */
template <typename _Tp> class list_model_listener {
	typedef _Tp value_type;
	typedef list_model<value_type> model_type;
	typedef vector<value_type> data_list_type;

public:

	class event {

	private:
		const model_type* m_source;

		data_list_type m_objects;

	public:
		event(const model_type* _source) :
						m_source(_source) {
		}

		event(const model_type* _source, value_type _object) :
						m_source(_source) {
			m_objects.push_back(_object);
		}

		event(const model_type* _source, data_list_type& _objects) :
						m_source(_source),
						m_objects(_objects) {

		}

		const model_type* get_source() {
			return m_source;
		}

		data_list_type& get_objects() {
			return m_objects;
		}

		value_type get_object() {
			if (m_objects.size() > 1) {
				throw new string("Event has more than one object");
			}
			return *m_objects.begin();
		}

	};

	virtual ~list_model_listener() {
	}

	virtual void values_cleared(event& event) = 0;

	virtual void values_added(event& event) = 0;

	virtual void values_removed(event& event) = 0;

	virtual void edition_cancelled(event& event) = 0;

	virtual void editions_started(event& event) = 0;

	virtual void editions_stopping(event& event) = 0;

	virtual void editions_stopped(event& event) = 0;

};

template<class _Tp> class list_model_view {

protected:
	typedef _Tp value_type;

public:
	/**
	 *
	 */
	class filter {

	public:
		virtual ~filter() {
		}
		virtual bool accept(const value_type _value) const = 0;

	};

	/**
	 *
	 */
	class comparator {

	public:
		virtual ~comparator() {
		}
		virtual int compare(const value_type _value1, const value_type value2) const = 0;

	};

	/**
	 *
	 */
	class owner {

	public:
		virtual ~owner() {
		}
		virtual list_model_view<value_type>* get_parent_view() = 0;

	};

	virtual ~list_model_view() {
	}

	virtual bool accept(const value_type _object) const = 0;

	virtual void attach(owner* _owner) = 0;

	virtual void detach(owner* _owner) = 0;

	// Compare
	virtual int compare(const value_type _o1, const value_type _o2) const = 0;
};

}

#endif /* ORG_SKYMARSHALL_LISTVIEW_HH_ */