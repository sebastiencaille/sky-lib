/*
 * binding_chain.hh
 *
 *  Created on: Sep 18, 2017
 *      Author: scaille
 */

#ifndef TYPES_HH_
#define TYPES_HH_

#include <stdexcept>
#include <memory>

namespace ch_skymarshall::gui {

using source_ptr = const void*;

class property;

class gui_exception: public std::exception {
private:
	std::string const m_what;
public:
	explicit gui_exception(std::string const& _message) :
			m_what(_message) {
	}

	gui_exception(gui_exception const& _original) :
			m_what(_original.what()) {
	}

	const char* what() const noexcept override {
		return m_what.c_str();
	}

	~gui_exception() override = default;
};

using gui_exception_ptr = std::shared_ptr<gui_exception const>;

}

#endif
