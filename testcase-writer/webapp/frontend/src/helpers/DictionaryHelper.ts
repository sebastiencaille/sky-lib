import { IdObject, TestDictionary, TestRole, TestAction, TestActor, TestParameterFactory } from '../webapis/Types'


const enhanceDictionary = (dict: TestDictionary): TestDictionary => {
	dict.actionsMap = dict.roles.flatMap(r => r.actions).reduce((a, v: TestAction) => a.set(v.id, v), new Map());
	dict.rolesMap = dict.roles.reduce((a, v: TestRole) => a.set(v.id, v), new Map());
	dict.actorsMap = dict.actors.reduce((a, v: TestActor) => a.set(v.id, v), new Map());
	dict.testObjectFactoriesMap = dict.testObjectFactories.reduce((a, v: TestParameterFactory) => a.set(v.id, v), new Map());
	dict.selectors = dict.selectorTypes.reduce((a, v: string) => a.add(v), new Set<string>());
	return dict;
}


const hasSelector = (dict: TestDictionary, action: TestAction): boolean => {
	return action.parameters[0] && dict.selectors.has(action.parameters[0].parameterType);
}

const descriptionOf = (dict: TestDictionary, testObjects: (IdObject|undefined)[], fallback ?: string) : string => {
	const descr = testObjects.map(o => o && dict.descriptions[o.id]?.description).find(descr => descr);
	return descr ?? fallback ?? '---';
}

const DictionaryHelper = {
	enhanceDictionary: enhanceDictionary,
	hasSelector: hasSelector,
	descriptionOf: descriptionOf
}

export default DictionaryHelper;