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


namespace org_skymarshall_util_hmi {

using namespace std;
using namespace __gnu_cxx;


/**
 * Allows to store and fire properties listeners
 */
class property_manager {
public:
	property_manager();
	void add_listener(const string& _name, property_listener* _listener);
	void remove_listener(const string& _name, property_listener* _listener);
	void remove_listener(const string& _name, property_listener_ref _listener);

	void fire_property_changed(const void* _source, const string& _name, const void* _oldValue, const void* _newValue) const;
	void fire_before_property_changed(const void* _source, property* _property) const;
	void fire_after_property_changed(const void* _source, property* _property) const;
	void dump() const;
private:
	typedef list<property_listener*> listener_list_type;
	typedef map<string, listener_list_type*> listener_map_type;
	typedef map<string, listener_list_type*>::const_iterator listeners_const_iter;

	listener_map_type m_propertyListeners;
};

}

#endif /* PropertyManager_HH_ */
