
/*
 * Properties.hh
 *
 *  Created on: Feb 19, 2012
 *      Author: scaille
 */

#ifndef PROPERTY_HH_
#define PROPERTY_HH_

#include <string>
#include <memory>

#include "property_listener.hh"
#include "property_manager.hh"

namespace ch_skymarshall::gui {

/**
 * Basic property
 */
class property {

private:
	bool m_attached = false;

protected:
	const string_view m_name;
	property_manager &m_manager;

public:

	property(const string_view &_name, property_manager &_manager);
	virtual ~property();

	const string_view& name() const;

	void add_listener(shared_ptr<property_listener> _listener);
	void remove_listener(weak_ptr<property_listener> _listener);

	virtual void attach() = 0;
};
}

#endif /* PROPERTY_HH_ */
