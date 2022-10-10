import DictionaryHelper from './DictionaryHelper.js';

const enhanceTestCase = (dict, tc) => {
	tc.references = tc.steps.map(step => step.reference).filter(ref => ref).reduce((a, v) => ({ ...a, [v.id]: v }), {});
	for (const step of tc.steps) {
		step.action = dict.actions[step.actionRef];
		for (const value of step.parametersValue) {
			value.testParameterFactory = dict.testObjectFactories[value.testParameterFactoryRef];
			if (!value.testParameterFactory) {
				value.testParameterFactory = tc.references[value.testParameterFactoryRef];
			}
		}
	}
	return tc;
}


const descriptionOfParameter = (dict, value) => {
	if (!value) {
		return '---';
	}
	return DictionaryHelper.descriptionOf(dict, [value.testParameterFactory], value.simpleValue);
}

const TestCaseHelper = {
	enhanceTestCase: enhanceTestCase,
	descriptionOfParameter: descriptionOfParameter
}

export default TestCaseHelper;