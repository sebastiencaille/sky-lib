import React from "react";
import { Metadata } from '../webapis/Types'

interface IMetadataChooserProps {
	allChoices: Metadata[];
	currentChoice ?: Metadata;
	onSelection: (metatadata?: Metadata) => void;
};

interface IMetadataChooserState {
	selectionedChoice?: Metadata
};

const initialState: IMetadataChooserState = {
	selectionedChoice: undefined
};


class DictionarySelector extends React.Component<IMetadataChooserProps, IMetadataChooserState> {

	constructor(props: IMetadataChooserProps) {
		super(props);
		this.state = initialState;
		this.changeSelection = this.changeSelection.bind(this);
		this.select = this.select.bind(this);
	}

	componentDidUpdate(prevProps: Readonly<IMetadataChooserProps>, prevState: Readonly<IMetadataChooserState>, snapshot?: any): void {
		let current: Metadata | undefined = undefined;
		if (this.props.currentChoice) {
			current = this.props.currentChoice;
		}
		if (!current && this.props.allChoices[0]) {
			current = this.props.allChoices[0];
		}
		if (current && current !== this.state.selectionedChoice) {
			this.setState({ selectionedChoice: current })
		}
	}

	private changeSelection(e: React.ChangeEvent<HTMLSelectElement>): void {
		this.setState({ selectionedChoice: this.props.allChoices.find(m => m.transientId === e.target.value) });
	}

	private select(): void {
		this.props.onSelection(this.state.selectionedChoice);
	}

	createItems = () => {
		let options = [];
		for (let dict of this.props.allChoices) {
			options.push(<option key={dict.transientId} value={dict.transientId}>{dict.description}</option>);
		}
		return options;
	}

	render() {
		return (<div>
			<select value={this.state.selectionedChoice?.transientId} onChange={this.changeSelection}>
				{this.createItems()}
			</select>
			<button onClick={this.select}>Select</button>
		</div>
		);
	}
}

export default DictionarySelector;
