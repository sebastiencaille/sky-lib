import React, { useCallback, useEffect, useMemo, useState } from 'react';
import Popup from 'reactjs-popup';
import 'reactjs-popup/dist/index.css';

import WebApis, { } from './webapis/WebApis';
import WebApiFeedback from './webapis/WebApiFeedback'
import { Metadata, TestDictionary, TestCase, ExportType, StepStatus } from './webapis/Types'
import Mappers from './mappers/Mappers';

import './App.css'

import MetadataChooser from './widgets/MetadataChooser';
import TestCaseTable from './widgets/TestCaseTable';
import { ApplicationStatusDisplay } from './widgets/ApplicationStatusDisplay';
import { ApplicationStatusProvider } from './contexts/ApplicationStatusContext';
import { StepStatusAction, clearStepStatuses, handleStepStatusAction, stepStatusUpdate } from './service/StepStatusService';

export default function App() {
	const [currentContextDictionary, setCurrentContextDictionary] = useState<Metadata>(undefined);
	const [currentContextTestCase, setCurrentContextTestCase] = useState<Metadata>(undefined);

	const [allDictionaries, setAllDictionaries] = useState<Metadata[]>([]);
	const [allTestCases, setAllTestCases] = useState<Metadata[]>([]);

	const [currentDictionary, setCurrentDictionary] = useState<TestDictionary | undefined>(undefined);
	const [currentTestCase, setCurrentTestCase] = useState<TestCase | undefined>(undefined);

	const [exportedTestCase, setExportedTestCase] = useState<string | undefined>(undefined);

	const [stepStatuses, setStepStatuses] = useState<Map<number, StepStatus>>(new Map());

	const tabId = useMemo(() => (Date.now().toString()), []);

	// On mount
	useEffect(() => {
		WebApis.listAllDictionaries((allMetaData) => setAllDictionaries(allMetaData));
		WebApis.loadCurrentContext((ctxt) => {
			setCurrentContextDictionary(ctxt.dictionary);
			setCurrentContextTestCase(ctxt.testCase);
		});
	}, [])


	useEffect(() => {
		if (currentContextDictionary) {
			WebApis.loadCurrentDictionary(dict => setCurrentDictionary(Mappers.enhanceDictionary(dict)));
		}
	}, [currentContextDictionary]);

	useEffect(() => {
		if (currentDictionary) {
			WebApis.listAllTestCases((allMetaData) => setAllTestCases(allMetaData));
		}
	}, [currentDictionary]);


	useEffect(() => {
		if (currentDictionary && currentContextTestCase) {
			WebApis.loadCurrentTestCase(tc => setCurrentTestCase(Mappers.enhanceTestCase(currentDictionary, tc)));
		}
	}, [currentDictionary, currentContextTestCase]);

	const dictionaryChanged = useCallback((metadata?: Metadata) => {
		if (!metadata) {
			return;
		}
		WebApis.selectCurrentDictionary(metadata.transientId, (context) => setCurrentContextDictionary(context.dictionary));
	}, []);

	const testCaseChanged = useCallback((metadata?: Metadata) => {
		if (!metadata) {
			return;
		}
		WebApis.selectCurrentTestCase(metadata.transientId, (context) => setCurrentContextTestCase(context.testCase));
	}, []);

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
			<ApplicationStatusProvider>
				<MetadataChooser
					prefix='dictionary'
					allMetadata={allDictionaries}
					initialySelectedMetadata={currentDictionary?.metadata}
					onSelection={dictionaryChanged} />
				<MetadataChooser
					prefix='testcase'
					allMetadata={allTestCases}
					initialySelectedMetadata={currentTestCase?.metadata}
					onSelection={testCaseChanged} />
				<button id='exportJava' onClick={exportJava}>Java Code</button>
				<button id='exportText' onClick={exportHumanReadable}>Human Readable</button>
				<button id='execute' onClick={executeTC}>Execute</button>
				<Popup open={exportedTestCase !== undefined} onClose={closePopUp}
					className="export-popup">
					<pre>
						<div>{exportedTestCase}</div>
					</pre>
				</Popup>
				<p>
					<TestCaseTable
						dictionary={currentDictionary}
						testCase={currentTestCase}
						stepStatuses={stepStatuses}
					/>
				</p>
				<div>
					<ApplicationStatusDisplay />
				</div>
				<WebApiFeedback tabId={tabId} updateStepStatus={stepUpdated} />
				<WebApis.ErrorHandler />
			</ApplicationStatusProvider>
		</div>
	)
}
