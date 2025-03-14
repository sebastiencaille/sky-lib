
import { useCallback, useEffect, useMemo, useState } from 'react';
import Popup from 'reactjs-popup';
import 'reactjs-popup/dist/index.css';

import WebApis from './webapis/WebApis';
import WebApiFeedback from './webapis/WebApiFeedback'
import { Metadata, TestDictionary, TestCase, ExportType, StepStatus } from './webapis/Types'
import Mappers from './mappers/Mappers';

import './App.css'

import MetadataChooser from './widgets/MetadataChooser';
import TestCaseTable from './widgets/TestCaseTable';
import { ApplicationStatusDisplay } from './widgets/ApplicationStatusDisplay';
import { StepStatusAction, clearStepStatuses, handleStepStatusAction, stepStatusUpdate } from './service/StepStatusService';
import { contextUpdate, useUserContext, useUserContextUpdater } from './contexts/UserContext';

export default function App() {

	const [allDictionaries, setAllDictionaries] = useState<Metadata[]>([]);
	const [allTestCases, setAllTestCases] = useState<Metadata[]>([]);

	const [currentDictionary, setCurrentDictionary] = useState<TestDictionary | undefined>(undefined);
	const [currentTestCase, setCurrentTestCase] = useState<TestCase | undefined>(undefined);

	const [exportedTestCase, setExportedTestCase] = useState<string | undefined>(undefined);

	const [stepStatuses, setStepStatuses] = useState<Map<number, StepStatus>>(new Map());

	const tabId = useMemo(() => (Date.now().toString()), []);

	const userContext = useUserContext();
	const contextUpdater = useUserContextUpdater();

	// On mount
	useEffect(() => {
		if (allDictionaries.length > 0) {
			return;
		}
		WebApis.listAllDictionaries((allMetaData) => setAllDictionaries(allMetaData));
		WebApis.loadCurrentContext((ctxt) => contextUpdater(contextUpdate(ctxt)));
	}, [contextUpdater])

	// Loads the dictionary when selected
	// TODO: 404 should clear the dictionary
	useEffect(() => {
		if (userContext.dictionary && userContext.dictionary !== currentDictionary?.metadata.transientId) {
			WebApis.loadDictionary(userContext.dictionary, dict => setCurrentDictionary(Mappers.enhanceDictionary(dict)));
		}
	}, [currentDictionary?.metadata.transientId, userContext.dictionary]);

	useEffect(() => {
		if (userContext.dictionary) {
			WebApis.listAllTestCases((allMetaData) => setAllTestCases(allMetaData));
		}
	}, [userContext.dictionary]);


	useEffect(() => {
		if (currentDictionary && userContext.testCase && userContext.testCase !== currentTestCase?.metadata.transientId) {
			WebApis.loadTestCase(userContext.testCase, tc => setCurrentTestCase(Mappers.enhanceTestCase(currentDictionary, tc)));
		}
	}, [currentDictionary, currentTestCase?.metadata.transientId, userContext]);

	// When a dictionary is selected
	const dictionaryChanged = useCallback((metadata?: Metadata) => {
		if (!metadata) {
			return;
		}
		const newContext = { ...userContext };
		newContext.dictionary = metadata.transientId;
		WebApis.validateContext(newContext, (context) => contextUpdater(contextUpdate(context)));
	}, [contextUpdater, userContext]);

	const testCaseChanged = useCallback((metadata?: Metadata) => {
		if (!metadata) {
			return;
		}
		const newContext = { ...userContext };
		newContext.testCase = metadata.transientId;
		WebApis.validateContext(newContext, (context) => contextUpdater(contextUpdate(context)));
	}, [contextUpdater, userContext]);

	const updateSteps = useCallback((action: StepStatusAction) => {
		setStepStatuses((currentStatuses) => handleStepStatusAction(currentStatuses, action));
	}, []);

	const executeTC = useCallback(() => {
		updateSteps(clearStepStatuses());
		WebApis.executeCurrentTestCase(tabId);
	}, [tabId, updateSteps]);

	const exportTC = useCallback((format: ExportType) => {
		WebApis.exportCurrentTestCase(format, (text) => setExportedTestCase(text));
	}, []);

	const exportJava = useCallback(() => {
		exportTC(ExportType.JAVA);
	}, [exportTC]);

	const exportHumanReadable = useCallback(() => {
		exportTC(ExportType.HUMAN_READABLE);
	}, [exportTC]);

	const stepUpdated = useCallback((step: StepStatus) => {
		updateSteps(stepStatusUpdate(step))
	}, [updateSteps]);


	const closePopUp = useCallback(() => {
		setExportedTestCase(undefined);
	}, []);

	return (
		<div className="App">
					<MetadataChooser
						prefix='dictionary'
						allMetadata={allDictionaries}
						initiallySelectedMetadata={currentDictionary?.metadata}
						onSelection={dictionaryChanged} />
					<MetadataChooser
						prefix='testcase'
						allMetadata={allTestCases}
						initiallySelectedMetadata={currentTestCase?.metadata}
						onSelection={testCaseChanged} />
					<button id='exportJava' onClick={exportJava} disabled={currentTestCase === undefined}>Java Code</button>
					<button id='exportText' onClick={exportHumanReadable}  disabled={currentTestCase === undefined}>Human Readable</button>
					<button id='execute' onClick={executeTC} disabled={currentTestCase === undefined}>Execute</button>
					<Popup open={exportedTestCase !== undefined} onClose={closePopUp}
						className="export-popup">
						<pre>
							<div>{exportedTestCase}</div>
						</pre>
					</Popup>
					<p />
					<TestCaseTable
						dictionary={currentDictionary}
						testCase={currentTestCase}
						stepStatuses={stepStatuses}
					/>
					<div>
						<ApplicationStatusDisplay />
					</div>
					<WebApiFeedback tabId={tabId} updateStepStatus={stepUpdated} />
					<WebApis.Component />

		</div>
	)
}
