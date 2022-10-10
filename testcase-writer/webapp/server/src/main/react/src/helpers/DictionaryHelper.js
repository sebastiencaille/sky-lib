
const enhanceDictionary = (dict) => {
	dict.actions = dict.roles.flatMap(r => r.actions).reduce((a, v) => ({ ...a, [v.id]: v }), {});
	dict.roles = dict.roles.reduce((a, v) => ({ ...a, [v.id]: v }), {});
	dict.actors = dict.actors.reduce((a, v) => ({ ...a, [v.id]: v }), {});
	dict.testObjectFactories = dict.testObjectFactories.reduce((a, v) => ({ ...a, [v.id]: v }), {});
	dict.selectors = dict.selectorTypes.reduce((a, v) => ({ ...a, [v]: true }), {});
	return dict;
}


const hasSelector = (dict, action) => {
	return action.parameters[0] && dict.selectors[action.parameters[0].parameterType];
}

const descriptionOf = (dict, testObjects, fallback) => {
	const descr = testObjects.filter(o => o).map(o => dict.descriptions[o.id]?.description).find(descr => descr);
	if (descr) {
		return descr;
	}
	return fallback;
}

const DictionaryHelper = {
	enhanceDictionary: enhanceDictionary,
	hasSelector: hasSelector,
	descriptionOf: descriptionOf
}

export default DictionaryHelper;