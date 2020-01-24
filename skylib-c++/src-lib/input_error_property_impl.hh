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
 * ErrorPropertyImpl.hh
 *
 *  Created on: Feb 28, 2012
 *      Author: scaille
 */

#ifndef ERRORPROPERTYIMPL_HH_
#define ERRORPROPERTYIMPL_HH_

#include <string>
#include "controller_property.hh"

namespace ch_skymarshall {
namespace gui {

using namespace std;

class input_error_property: public error_notifier, public controller_property<gui_exception_ptr> {
public:
	input_error_property(const string& _name, property_manager& _manager) :
			controller_property<gui_exception_ptr>(_name, _manager, NULL,
					this) {
	}

	void set_error(source_ptr _source, const gui_exception& _value) {
		gui_exception_ptr oldValue = get();
		this->set(_source, new gui_exception(_value));
		if (oldValue != NULL) {
			delete oldValue;
		}
	}
};

}
}
#endif /* ERRORPROPERTYIMPL_HH_ */
