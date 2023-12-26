import React from 'react';
import Popup from 'reactjs-popup';
import 'reactjs-popup/dist/index.css';

import WebApis from './webapis/WebApis';
import WebApiFeedback from './webapis/WebApiFeedback'
import { Metadata, TestDictionary, TestCase, Context, ExportType, StepStatus } from './webapis/Types'
import Mappers from './mappers/Mappers';

import './App.css'

import MetadataChooser from './widgets/MetadataChooser';
import TestCaseTable from './widgets/TestCaseTable';
import { ApplicationStatusDisplay } from './widgets/ApplicationStatusDisplay';
import { ApplicationStatusProvider } from './contexts/ApplicationStatusContext';
import { StepStatusAction, clearStepStatuses, handleStepStatusAction, stepStatusUpdate } from './service/StepStatusService';

interface IAppProps {

}

interface IAppState {
	currentContext?: Context;
	allDictionaries: Metadata[];
	allTestCases: Metadata[];
	currentDictionary?: TestDictionary;
	currentTestCase?: TestCase;
	exportedTestCase?: string;

	stepStatuses: Map<number, StepStatus>;
}

const initialState: IAppState = {

	currentContext: undefined,
	allDictionaries: [],
	allTestCases: [],
	currentDictionary: undefined,
	currentTestCase: undefined,
	exportedTestCase: undefined,
	stepStatuses: new Map()
};

class App extends React.Component<IAppProps, IAppState> {

	constructor(props: IAppProps) {
		super(props);
		this.state = initialState;
		this.dictionaryChanged = this.dictionaryChanged.bind(this);
		this.testCaseChanged = this.testCaseChanged.bind(this);
		this.stepUpdated = this.stepUpdated.bind(this);
	}

	componentDidMount(): void {
		WebApis.listAllDictionaries((allMetaData) => this.setState({ allDictionaries: allMetaData }));
		WebApis.loadCurrentContext((c) => this.setState({ currentContext: c }))
	}

	componentDidUpdate(_prevProp: Readonly<IAppProps>, prevState: Readonly<IAppState>): void {
		const contextDicoChanged = prevState.currentContext?.dictionary !== this.state.currentContext?.dictionary;
		const contextTcChanged = prevState.currentContext?.testCase !== this.state.currentContext?.testCase;
		const dicoChanged = prevState.currentDictionary !== this.state.currentDictionary && this.state.currentDictionary;

		if (contextDicoChanged && this.state.currentContext?.dictionary) {
			this.process(() => WebApis.loadCurrentDictionary(dict => this.setState({ currentDictionary: Mappers.enhanceDictionary(dict) })));
		}
		if (dicoChanged) {
			this.process(() => WebApis.listAllTestCases((allMetaData) => this.setState({ allTestCases: allMetaData })));
		}
		if (dicoChanged || contextTcChanged) {
			if (this.state.currentDictionary && this.state.currentContext?.testCase) {
				this.process(() => WebApis.loadCurrentTestCase(tc =>
					this.setState((ps) => ({ currentTestCase: Mappers.enhanceTestCase(ps.currentDictionary!, tc) }))));
			}
		}
	}

	private process(call: () => void) {
		call.apply(this);

	}

	private dictionaryChanged = (metadata?: Metadata) => {
		if (!metadata) {
			return;
		}
		this.process(() => WebApis.selectCurrentDictionary(metadata.transientId, (context) => this.setState({ currentContext: context })));
	}

	private testCaseChanged = (metadata?: Metadata) => {
		if (!metadata) {
			return;
		}
		this.process(() => WebApis.selectCurrentTestCase(metadata.transientId, (context) => this.setState({ currentContext: context })));
	}

	private execute = () => {
		this.updateSteps(clearStepStatuses());
		this.process(() => WebApis.executeCurrentTestCase());
	}

	private export = (format: ExportType) => {
		this.process(() => WebApis.exportCurrentTestCase(format, (text) => this.setState({ exportedTestCase: text })));
	}

	private exportJava = () => {
		this.export(ExportType.JAVA);
	}


	private exportHumanReadable = () => {
		this.export(ExportType.HUMAN_READABLE)
	}


	private stepUpdated = (step: StepStatus) => {
		this.updateSteps(stepStatusUpdate(step))
	}

	private updateSteps = (action: StepStatusAction) => {
		this.setState((ps) => ({ stepStatuses: handleStepStatusAction(ps.stepStatuses, action) }));
	}

	private closePopUp = () => {
		this.setState({ exportedTestCase: undefined });
	}

	render() {
		return (
			<div className="App">
				<ApplicationStatusProvider>
					<MetadataChooser
						prefix='dictionary'
						allChoices={this.state.allDictionaries}
						currentChoice={this.state.currentDictionary?.metadata}
						onSelection={this.dictionaryChanged} />
					<MetadataChooser
						prefix='testcase'
						allChoices={this.state.allTestCases}
						currentChoice={this.state.currentTestCase?.metadata}
						onSelection={this.testCaseChanged} />
					<button id='exportJava' onClick={this.exportJava}>Java Code</button>
					<button id='exportText' onClick={this.exportHumanReadable}>Human Readable</button>
					<button id='execute' onClick={this.execute}>Execute</button>
					<ApplicationStatusDisplay />
					<Popup open={this.state.exportedTestCase !== undefined} onClose={this.closePopUp}
						className="export-popup">
						<pre>
							<div>{this.state.exportedTestCase}</div>
						</pre>
					</Popup>
					<TestCaseTable
						dictionary={this.state.currentDictionary}
						testCase={this.state.currentTestCase}
						stepStatuses={this.state.stepStatuses}
					/>
					<WebApiFeedback stepStatusUpdate={this.stepUpdated} />
				</ApplicationStatusProvider>
			</div>
		)
	}
}

export default App;
