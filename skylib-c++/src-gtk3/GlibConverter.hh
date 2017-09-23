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

#ifndef GLIBCONVERTER_HH_
#define GLIBCONVERTER_HH_

#include <binding_interface.hh>
#include <glibmm.h>
#include <string>


namespace org_skymarshall_util_hmi_glib {

using namespace std;
using namespace Glib;
using namespace org_skymarshall_util_hmi;

class string_to_ustring_converter:
		public binding_converter<string, Glib::ustring> {

protected:

	virtual ~string_to_ustring_converter();

public:
	string_to_ustring_converter();
	const string convert_component_value_to_property_value(const Glib::ustring _componentValue);
	const Glib::ustring convert_property_value_to_component_value(const string _propertyValue);

};

}
#endif /* GLIBCONVERTER_HH_ */
