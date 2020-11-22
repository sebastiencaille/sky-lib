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
 * PropertyManager.hh
 *
 *  Created on: Feb 18, 2012
 *      Author: scaille
 */

#ifndef PropertyManager_HH_
#define PropertyManager_HH_

#include<list>
#include<map>
#include<string>

#include "property_listener.hh"

namespace ch_skymarshall::gui {

using namespace std;
using namespace __gnu_cxx;

/**
 * Allows to store and fire properties listeners
 */
class property_manager {
public:
	property_manager();
	~property_manager();

	void add_listener(const string &_name,
			shared_ptr<property_listener> _listener);

	void remove_listeners(const string& _name);
	void remove_listener(const string &_name,
			weak_ptr<property_listener> _listener);
	void remove_listener(const string &_name, property_listener_ref _listener);

	void fire_property_changed(source_ptr _source, const string &_name,
			const void *_oldValue, const void *_newValue) const;
	void fire_before_property_changed(source_ptr _source,
			property *_property) const;
	void fire_after_property_changed(source_ptr _source,
			property *_property) const;
	void dump() const;
private:
	using listener_list_type = list<shared_ptr<property_listener>>;
	using listener_map_type = map<string, shared_ptr<listener_list_type>>;
	using listeners_const_iter = listener_map_type::const_iterator;
	listener_map_type m_propertyListeners;
};

}

#endif /* PropertyManager_HH_ */
