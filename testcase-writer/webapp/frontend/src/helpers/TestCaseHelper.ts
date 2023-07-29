import { TestDictionary, TestActor, TestAction, TestParameterFactory, TestCase, TestReference, TestParameterValue } from '../webapis/Types';
import DictionaryHelper from './DictionaryHelper';

const enhanceTestCase = (dict: TestDictionary, tc: TestCase): TestCase => {
	tc.references = tc.steps.map(step => step.reference).filter(ref => ref).reduce((a, v: TestReference) => a.set(v.id, v), new Map());
	for (const step of tc.steps) {
		step.action = dict.actionsMap.get(step.actionRef) as TestAction;
		step.actor = dict.actorsMap.get(step.actorRef) as TestActor;
		for (const value of step.parametersValue) {
			if (value.testParameterFactoryRef) {
				value.testParameterFactory = dict.testObjectFactoriesMap.get(value.testParameterFactoryRef) as TestParameterFactory;
				if (!value.testParameterFactory) {
					value.testParameterFactory = tc.references.get(value.testParameterFactoryRef) as TestParameterFactory;
				}
			}
		}
	}
	return tc;
}


const descriptionOfParameter = (dict: TestDictionary, value?: TestParameterValue): string => {
	return DictionaryHelper.descriptionOf(dict, [value?.testParameterFactory], value?.simpleValue);
}

const TestCaseHelper = {
	enhanceTestCase: enhanceTestCase,
	descriptionOfParameter: descriptionOfParameter
}

export default TestCaseHelper;