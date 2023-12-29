import React, { useEffect, useState } from 'react';
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
import { ApplicationStatusProvider } from './contexts/ApplicationStatusContext';
import { StepStatusAction, clearStepStatuses, handleStepStatusAction, stepStatusUpdate } from './service/StepStatusService';

function process(call: () => void) {
	call();
}

export default function App() {
	const [currentContextDictionary, setCurrentContextDictionary] = useState<Metadata>(undefined);
	const [currentContextTestCase, setCurrentContextTestCase] = useState<Metadata>(undefined);

	const [allDictionaries, setAllDictionaries] = useState<Metadata[]>([]);
	const [allTestCases, setAllTestCases] = useState<Metadata[]>([]);

	const [currentDictionary, setCurrentDictionary] = useState<TestDictionary | undefined>(undefined);
	const [currentTestCase, setCurrentTestCase] = useState<TestCase | undefined>(undefined);

	const [exportedTestCase, setExportedTestCase] = useState<string | undefined>(undefined);
	
	const [stepStatuses, setStepStatuses] = useState<Map<number, StepStatus>>(new Map());

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
			process(() => WebApis.loadCurrentDictionary(dict => setCurrentDictionary(Mappers.enhanceDictionary(dict))));
		}
	}, [currentContextDictionary]);

	useEffect(() => {
		if (currentDictionary) {
			process(() => WebApis.listAllTestCases((allMetaData) => setAllTestCases(allMetaData)));
		}
	}, [currentDictionary]);


	useEffect(() => {
		if (currentDictionary && currentContextTestCase) {
			process(() => WebApis.loadCurrentTestCase(tc => setCurrentTestCase(Mappers.enhanceTestCase(currentDictionary, tc))));
		}
	}, [currentDictionary, currentContextTestCase]);

	const dictionaryChanged = (metadata?: Metadata) => {
		if (!metadata) {
			return;
		}
		process(() => WebApis.selectCurrentDictionary(metadata.transientId, (context) => setCurrentContextDictionary(context.dictionary)));
	}

	const testCaseChanged = (metadata?: Metadata) => {
		if (!metadata) {
			return;
		}
		process(() => WebApis.selectCurrentTestCase(metadata.transientId, (context) => setCurrentContextTestCase(context.testCase)));
	}

	const executeTC = () => {
		updateSteps(clearStepStatuses());
		process(() => WebApis.executeCurrentTestCase());
	}

	const exportTC = (format: ExportType) => {
		process(() => WebApis.exportCurrentTestCase(format, (text) => setExportedTestCase(text)));
	}

	const exportJava = () => {
		exportTC(ExportType.JAVA);
	}


	const exportHumanReadable = () => {
		exportTC(ExportType.HUMAN_READABLE);
	}


	const stepUpdated = (step: StepStatus) => {
		updateSteps(stepStatusUpdate(step))
	}

	const updateSteps = (action: StepStatusAction) => {
		setStepStatuses((currentStatuses) => handleStepStatusAction(currentStatuses, action));
	}

	const closePopUp = () => {
		setExportedTestCase(undefined);
	}

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
				<ApplicationStatusDisplay />
				<Popup open={exportedTestCase !== undefined} onClose={closePopUp}
					className="export-popup">
					<pre>
						<div>{exportedTestCase}</div>
					</pre>
				</Popup>
				<TestCaseTable
					dictionary={currentDictionary}
					testCase={currentTestCase}
					stepStatuses={stepStatuses}
				/>
				<WebApiFeedback stepStatusUpdate={stepUpdated} />
			</ApplicationStatusProvider>
		</div>
	)
}
