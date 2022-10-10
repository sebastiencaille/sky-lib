const enhanceTestCase = (dict, tc) => {
	for (const step of tc.steps) {
		step.action = dict.actions[step.actionRef];
	}
	return tc;
}


const descriptionOfParameter = (dict, value) => {
	if (!value) {
		return '---';
	}
	let description = dict.descriptions[value.apiParameterId]?.description;
	if (!description) {
		description = dict.descriptions[value.testParameterFactoryRef]?.description;
	}
	if (!description) {
		description = value.simpleValue;
	}

	return description;
}

const TestCaseHelper = {
	enhanceTestCase: enhanceTestCase,
	descriptionOfParameter: descriptionOfParameter
}

export default TestCaseHelper;