import {
	TestDictionary,
	TestRole,
	TestAction,
	TestActor,
	TestParameterFactory,
	TestCase,
	TestReference
} from '../webapis/Types'

const enhanceDictionary = (dict: TestDictionary): TestDictionary => {
	dict.actionsMap = dict.roles
		.flatMap(r => (r.actions as Array<TestAction>))
		.reduce((map, action) => map.set(action.id, action), new Map());
	dict.rolesMap = (dict.roles as Array<TestRole>)
		.reduce((a, v) => a.set(v.id, v), new Map());
	dict.actorsMap = (dict.actors as Array<TestActor>).reduce((a, v) => a.set(v.id, v), new Map());
	dict.testObjectFactoriesMap = (dict.testObjectFactories as Array<TestParameterFactory>).reduce((a, v) => a.set(v.id, v), new Map());
	dict.selectors = (dict.selectorTypes as Array<string>).reduce((a, v) => a.add(v), new Set<string>());
	return dict;
}

const enhanceTestCase = (dict: TestDictionary, tc: TestCase): TestCase => {
	tc.references = tc.steps.map(step => (step.reference as TestReference))
		.filter(ref => ref)
		.reduce((a, v) => a.set(v.id, v), new Map<string, TestReference>());
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


const Mappers = {
	enhanceDictionary: enhanceDictionary,
	enhanceTestCase: enhanceTestCase
}

export default Mappers;