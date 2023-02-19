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
 * IntConverters.hh
 *
 *  Created on: Mar 4, 2012
 *      Author: scaille
 */

#ifndef INT_CONVERTERS_HH_
#define INT_CONVERTERS_HH_

#include <string>

#include "binding_interface.hh"

namespace ch_skymarshall::gui::converters {

using namespace std;

class int_to_string: public binding_converter<int, string> {

public:
	int_to_string();

	~int_to_string() override;

	int convert_component_value_to_property_value(
			const string _componentValue) override;

	string convert_property_value_to_component_value(
			const int _propertyValue) override;

	static shared_ptr<binding_converter<int, string>> of() {
		return make_shared<int_to_string>();
	}
};

}

#endif /* INT_CONVERTERS_HH_ */

