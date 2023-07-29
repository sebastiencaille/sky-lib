import { components } from '@scaille/testcase-writer-webapi'

type Metadata = components["schemas"]["Metadata"];
type IdObject = components["schemas"]["IdObject"];
type ApiTestDictionary = components["schemas"]["TestDictionary"];
type TestAction = components["schemas"]["TestAction"];
type TestActor = components["schemas"]["TestActor"];
type TestRole = components["schemas"]["TestRole"];
type TestParameterFactory = components["schemas"]["TestParameterFactory"];
type ApiTestCase = components["schemas"]["TestCase"];
type ApiTestStep = components["schemas"]["TestStep"];
type ApiTestParameterValue = components["schemas"]["TestParameterValue"];
type TestReference = components["schemas"]["TestReference"];
type Context = components["schemas"]["Context"];
type TestObjectDescription = components["schemas"]["TestObjectDescription"];
type StepStatus = components["schemas"]["StepStatus"];

interface TestDictionary extends ApiTestDictionary {

	actionsMap: Map<string, TestAction>;
	rolesMap: Map<string, TestRole>;
	actorsMap: Map<string, TestActor>;
	testObjectFactoriesMap: Map<string, TestParameterFactory>;
	selectors: Set<string>;

}
interface TestCase extends ApiTestCase {
	references: Map<string, TestReference>;
	steps: TestStep[];
}

interface TestStep extends ApiTestStep  {
	action: TestAction;
	actor: TestActor;
	parametersValue: TestParameterValue[];
}

interface TestParameterValue extends ApiTestParameterValue  {
	testParameterFactory: TestParameterFactory;
}

export type {
	Metadata, IdObject,
	TestDictionary, TestAction, TestActor, TestRole, TestParameterFactory, TestObjectDescription,
	TestCase, TestStep, TestReference, TestParameterValue,
	StepStatus,
	Context
}