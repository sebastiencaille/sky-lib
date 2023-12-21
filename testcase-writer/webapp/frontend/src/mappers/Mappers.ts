import { TestDictionary, TestRole, TestAction, TestActor, TestParameterFactory } from '../webapis/Types'

const enhanceDictionary = (dict: TestDictionary): TestDictionary => {
	dict.actionsMap = dict.roles.flatMap(r => r.actions).reduce((a, v: TestAction) => a.set(v.id, v), new Map());
	dict.rolesMap = dict.roles.reduce((a, v: TestRole) => a.set(v.id, v), new Map());
	dict.actorsMap = dict.actors.reduce((a, v: TestActor) => a.set(v.id, v), new Map());
	dict.testObjectFactoriesMap = dict.testObjectFactories.reduce((a, v: TestParameterFactory) => a.set(v.id, v), new Map());
	dict.selectors = dict.selectorTypes.reduce((a, v: string) => a.add(v), new Set<string>());
	return dict;
}

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


const Mappers = {
	enhanceDictionary: enhanceDictionary,
	enhanceTestCase: enhanceTestCase
}

export default Mappers;