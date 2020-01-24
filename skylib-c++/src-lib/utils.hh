/*
 * utils.hh
 *
 *  Created on: Jan 24, 2020
 *      Author: scaille
 */

#ifndef UTILS_HH_
#define UTILS_HH_

#include <vector>

namespace ch_skymarshall {
namespace util {

using std::vector;

template<class Input, class UnaryFunction>
constexpr UnaryFunction for_each_vector(vector<Input>& vec, UnaryFunction f) {
	typename vector<Input>::iterator iter;
	for (Input& input: vec) {
		f(input);
	}
	return f; // implicit move since C++11
}
}
}

#endif /* UTILS_HH_ */
