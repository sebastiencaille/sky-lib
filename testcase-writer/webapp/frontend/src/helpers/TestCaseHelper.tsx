import {  IdObject, TestParameterValue, TestDictionary, TestAction } from '../webapis/Types'


const hasSelector = (dict: TestDictionary, action: TestAction): boolean => {
	return action.parameters[0] && dict.selectors.has(action.parameters[0].parameterType);
}


const descriptionOf = (dict: TestDictionary, testObjects: (IdObject|undefined)[], fallback ?: string) : string => {
	const descr = testObjects.map(o => o && dict.descriptions[o.id]?.description).find(descr => descr);
	return descr ?? fallback ?? '---';
}


const descriptionOfParameter = (dict: TestDictionary, value?: TestParameterValue): string => {
	return descriptionOf(dict, [value?.testParameterFactory], value?.simpleValue);
}

const TestCaseHelper = {
	hasSelector: hasSelector,
	descriptionOf: descriptionOf,
	descriptionOfParameter: descriptionOfParameter
}

export default TestCaseHelper;