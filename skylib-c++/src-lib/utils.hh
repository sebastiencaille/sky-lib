/*
 * utils.hh
 *
 *  Created on: Jan 24, 2020
 *      Author: scaille
 */

#ifndef UTILS_HH_
#define UTILS_HH_

#include <vector>
#include <memory>

namespace ch_skymarshall::util {

#ifdef DEBUG_DESTR
#define DESTR_LOG(p) cout << p << endl;
#define DESTR_WITH_LOG(p) { DESTR_LOG(p); }
#endif

#ifndef DEBUG_DESTR
#define DESTR_LOG(p) (void)0
#define DESTR_WITH_LOG(p) = default;;
#endif

template<class Input, class UnaryFunction>
constexpr UnaryFunction for_each_vector(std::vector<Input> &vec, UnaryFunction f) {
	typename std::vector<Input>::iterator iter;
	for (Input &input : vec) {
		f(input);
	}
	return f; // implicit move since C++11
}

template<typename _T> std::shared_ptr<_T> withLazy(std::weak_ptr<_T> &_lazy,
		std::weak_ptr<void> const &_owner,
		std::function<std::shared_ptr<_T>()> _allocator) {
	std::shared_ptr<_T> listener;
	if (auto existing = _lazy.lock()) {
		listener = existing;
	} else {
		listener = _allocator();
		_lazy = existing;
	}
	return listener;
}

}

#endif /* UTILS_HH_ */
