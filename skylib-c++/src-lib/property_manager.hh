
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

	void add_listener(const string_view &_name,
			shared_ptr<property_listener> _listener);

	void remove_listeners(const string_view & _name);
	void remove_listener(const string_view &_name,
			weak_ptr<property_listener> _listener);
	void remove_listener(const string_view &_name, property_listener_ref _listener);

	void fire_property_changed(source_ptr _source, const string_view &_name,
			const void *_oldValue, const void *_newValue) const;
	void fire_before_property_changed(source_ptr _source,
			property *_property) const;
	void fire_after_property_changed(source_ptr _source,
			property *_property) const;
	void dump() const;
private:
	using listener_list_type = list<shared_ptr<property_listener>>;
	using listener_map_type = map<string_view, shared_ptr<listener_list_type>>;
	listener_map_type m_propertyListeners;
};

}

#endif /* PropertyManager_HH_ */
