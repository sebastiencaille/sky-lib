import React from 'react';
import Popup from 'reactjs-popup';
import 'reactjs-popup/dist/index.css';

import WebApis from './webapis/WebApis';
import WebApiFeedback from './webapis/WebApiFeedback'
import { Metadata, TestDictionary, TestCase, Context, StepStatus, ExportType } from './webapis/Types'
import Mappers from './mappers/Mappers';

import { ErrorState, defaultErrorHandler } from './service/Errors';
import './App.css'

import MetadataChooser from './widgets/MetadataChooser';
import TestCaseTable from './widgets/TestCaseTable';
import ErrorDisplay from './widgets/ErrorDisplay';

interface IAppProps {

}

interface IAppState {
	currentContext?: Context;
	allDictionaries: Metadata[];
	allTestCases: Metadata[];
	currentDictionary?: TestDictionary;
	currentTestCase?: TestCase;
	executionState: string[];
	stepStatuses: Map<number, StepStatus>;
	displayedExport?: string;

	errors: ErrorState;
}

const initialState: IAppState = {

	currentContext: undefined,
	allDictionaries: [],
	allTestCases: [],
	currentDictionary: undefined,
	currentTestCase: undefined,
	executionState: [],
	stepStatuses: new Map<number, StepStatus>(),
	displayedExport: undefined,

	errors: ErrorState.empty()
};

class App extends React.Component<IAppProps, IAppState> {

	constructor(props: IAppProps) {
		super(props);
		this.state = initialState;
		this.dictionaryChanged = this.dictionaryChanged.bind(this);
		this.testCaseChanged = this.testCaseChanged.bind(this);
		this.stepStatusChanged = this.stepStatusChanged.bind(this);
		defaultErrorHandler.errorHandler = ( (error: string) => this.setState(prevState => ({ errors: prevState.errors.add(error) })));
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
		this.setState({ errors: ErrorState.empty() });
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
		this.process(() => WebApis.executeCurrentTestCase());
	}

	private export = (format: ExportType) => {
		this.process(() => WebApis.exportCurrentTestCase(format, (text) => this.setState({ displayedExport: text })));
	}

	private stepStatusChanged = (stepStatus: StepStatus) => {
		if (stepStatus) {
			this.setState(prevState => {
				const newStatuses = new Map(prevState.stepStatuses)
				newStatuses.set(stepStatus?.ordinal, stepStatus);
				return { stepStatuses: newStatuses }
			});
		}
	}

	render() {
		return (
			<div className="App">
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
				<button id='exportJava' onClick={() => this.export(ExportType.JAVA)}>Java Code</button>
				<button id='exportText' onClick={() => this.export(ExportType.HUMAN_READABLE)}>Human Readable</button>
				<button id='execute' onClick={this.execute}>Execute</button>
				<ErrorDisplay errors={this.state.errors} />
				<Popup open={this.state.displayedExport !== undefined} onClose={() => this.setState({ displayedExport: undefined })}
					className="export-popup">
					<pre>
						<div>{this.state.displayedExport}</div>
					</pre>
				</Popup>
				<TestCaseTable
					dictionary={this.state.currentDictionary}
					testCase={this.state.currentTestCase}
					stepStatuses={this.state.stepStatuses}
				/>
				<WebApiFeedback
					stepStatusChanged={this.stepStatusChanged}
				/>
			</div>
		)
	}
}

export default App;
