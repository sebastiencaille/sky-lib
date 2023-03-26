import React from 'react';
import DictionaryHelper from './helpers/DictionaryHelper';
import TestCaseHelper from './helpers/TestCaseHelper';
import MetadataChooser from './widgets/MetadataChooser';
import TestCaseTable from './widgets/TestCaseTable';
import WebApis from './webapis/WebApis';
import WebApiFeedback from './webapis/WebApiFeedback'
import { Metadata, TestDictionary, TestCase, Context, StepStatus } from './webapis/Types'
import './App.css';

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
}

const initialState: IAppState = {
	currentContext: undefined,
	allDictionaries: [],
	allTestCases: [],
	currentDictionary: undefined,
	currentTestCase: undefined,
	executionState: [],
	stepStatuses: new Map<number, StepStatus>(),
};

class App extends React.Component<IAppProps, IAppState> {

	constructor(props: any) {
		super(props);
		this.state = initialState;
		this.dictionaryChanged = this.dictionaryChanged.bind(this);
		this.testCaseChanged = this.testCaseChanged.bind(this);
		this.stepStatusChanged = this.stepStatusChanged.bind(this);
	}

	componentDidMount(): void {
		WebApis.listAllDictionaries((allMetaData) => this.setState({ allDictionaries: allMetaData }));
		WebApis.loadCurrentContext((c) => this.setState({ currentContext: c}))
  	}

	componentDidUpdate(prevProps: Readonly<IAppProps>, prevState: Readonly<IAppState>): void {
		const contextDicoChanged = prevState.currentContext?.dictionary !== this.state.currentContext?.dictionary;
		const contextTcChanged = prevState.currentContext?.testCase !== this.state.currentContext?.testCase;
		const dicoChanged = prevState.currentDictionary !== this.state.currentDictionary && this.state.currentDictionary;
		
		if (contextDicoChanged && this.state.currentContext?.dictionary) {
			WebApis.loadCurrentDictionary(dict => this.setState({ currentDictionary: DictionaryHelper.enhanceDictionary(dict) }));
		}
		if (dicoChanged) {
			WebApis.listAllTestCases((allMetaData) => this.setState({ allTestCases: allMetaData }));
		}
		if (dicoChanged || contextTcChanged) {
			if (this.state.currentDictionary && this.state.currentContext?.testCase) {
				WebApis.loadCurrentTestCase(tc =>
					this.setState({ currentTestCase: TestCaseHelper.enhanceTestCase(this.state.currentDictionary!, tc) }));
			}
		}
	}

	private dictionaryChanged = (metadata?: Metadata) => {
		if (!metadata) {
			return;
		}
		WebApis.selectCurrentDictionary(metadata.transientId, (context) => this.setState({ currentContext: context }));
	}

	private testCaseChanged = (metadata?: Metadata) => {
		if (!metadata) {
			return;
		}
		WebApis.selectCurrentTestCase(metadata.transientId, (context) => this.setState({ currentContext: context }));
	}

	private execute = () => {
  		WebApis.executeCurrentTestCase();
	}

	private stepStatusChanged = (stepStatus: StepStatus) =>  {
		if (stepStatus) {
			this.state.stepStatuses.set(stepStatus?.ordinal, stepStatus);
			this.setState({ stepStatuses: new Map(this.state.stepStatuses)});
		}
	}

	render() {
		return (
			<div className="App">
				<MetadataChooser
					allChoices={this.state.allDictionaries}
					currentChoice={this.state.currentDictionary?.metadata}
					onSelection={this.dictionaryChanged} />
				<MetadataChooser
					allChoices={this.state.allTestCases}
					currentChoice={this.state.currentTestCase?.metadata}
					onSelection={this.testCaseChanged} />
				<button onClick={this.execute}>Execute</button>
				<TestCaseTable
					dictionary={this.state.currentDictionary}
					testCase={this.state.currentTestCase} 
					stepStatuses={this.state.stepStatuses}
					/>
        		<WebApiFeedback 
        			stepStatusChanged={ this.stepStatusChanged }         		
        		/>
    		</div>
        )
	}
}

export default App;
