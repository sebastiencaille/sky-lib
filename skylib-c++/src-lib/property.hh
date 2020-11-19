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
	const string m_name;
	property_manager &m_manager;

public:

	property(const string &_name, property_manager &_manager);
	property(const char *_name, property_manager &_manager);
	virtual ~property();

	const string& name() const;

	void add_listener(shared_ptr<property_listener> _listener);
	void remove_listener(shared_ptr<property_listener> _listener);
	void remove_listener(shared_ptr<property_listener_ref> _listener);

	virtual void attach() = 0;
};
}

#endif /* PROPERTY_HH_ */
