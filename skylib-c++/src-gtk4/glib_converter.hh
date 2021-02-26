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
 * GlibConverter.hh
 *
 *  Created on: Apr 4, 2012
 *      Author: scaille
 */

#ifndef GLIB_CONVERTER_HH_
#define GLIB_CONVERTER_HH_

#include <glibmm.h>
#include <string>

#include "binding_interface.hh"

namespace ch_skymarshall::gui::glib {

using namespace std;
using namespace Glib;

class string_to_ustring: public binding_converter<string, ustring> {

public:
	string_to_ustring();

	virtual ~string_to_ustring();

	const string convert_component_value_to_property_value(
			const ustring _componentValue);
	const ustring convert_property_value_to_component_value(
			const string _propertyValue);

	static shared_ptr<binding_converter<string, ustring>> of() {
		return make_shared<string_to_ustring>();
	}
};

}
#endif /* GLIB_CONVERTER_HH_ */
