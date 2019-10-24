/*
 * binding_chain.hh
 *
 *  Created on: Sep 18, 2017
 *      Author: scaille
 */

#ifndef TYPES_HH_
#define TYPES_HH_

#include <stdexcept>

namespace org_skymarshall_util_hmi {

typedef const void* source_ptr;

class property;

class hmi_exception: public std::exception {
private:
	std::string const m_what;
public:
	hmi_exception(std::string const& _message) :
			m_what(_message) {
	}

	hmi_exception(hmi_exception const& _original) :
			m_what(_original.what()) {
	}

	virtual const char* what() const noexcept {
		return m_what.c_str();
	}

	~hmi_exception() = default;
};

typedef hmi_exception* hmi_exception_ptr;

}

#endif
