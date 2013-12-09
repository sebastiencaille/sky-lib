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

#ifndef INTCONVERTERS_HH_
#define INTCONVERTERS_HH_

#include <string>
#include <Converter.hh>
#include <stdlib.h>
#include <limits.h>
#include <stdio.h>
#include <errno.h>
 #include <string.h>
#include <sstream>

namespace org_skymarshall_util_hmi {

using namespace std;

class int_to_string: public binding_converter<int, string> {
public:
	int_to_string(component_binding<string>* _binding, bool _bindingDeletedOnDelete) :
			binding_converter(_binding, _bindingDeletedOnDelete) {

	}

	int convertComponentValueToObject(const string& _componentObject) {
		char* endPtr;
		errno = 0;
		long result = strtol(_componentObject.c_str(), &endPtr, 10);
		if (errno != 0) {
			m_errorProperty->setValue(m_binding, string(strerror(errno)));
		}
		return (int) result;
	}

	string convertObjectToComponentValue(const int& value) {
		std::stringstream ss;
		ss << value;
		return ss.str();
	}

};

}

#endif /* INTCONVERTERS_HH_ */
