import React, { useEffect, useState } from "react";
import { Metadata } from '../webapis/Types'

interface IMetadataChooserProps {
	prefix: string;
	allMetadata: Metadata[];
	initialySelectedMetadata?: Metadata;
	onSelection: (metatadata?: Metadata) => void;
};

export default function MetadataChooser(props: Readonly<IMetadataChooserProps>) {

	const [selectedMetadata, setSelectedMetadata] = useState<Metadata>(undefined);

	useEffect(() => {

		let current: Metadata | undefined = undefined;
		if (props.initialySelectedMetadata) {
			current = props.initialySelectedMetadata;
		}
		if (!current && props.allMetadata[0]) {
			current = props.allMetadata[0];
		}
		if (current && current !== selectedMetadata) {
			setSelectedMetadata(current);
		}
	}, [props.allMetadata, props.initialySelectedMetadata, selectedMetadata]);

	const changeSelection = (e: React.ChangeEvent<HTMLSelectElement>) => {
		setSelectedMetadata(props.allMetadata.find((m) => (m.transientId === e.target.value)));
	}

	const select = () => {
		props.onSelection(selectedMetadata);
	}

	const createItems = () => {
		const options = [];
		for (const metadata of props.allMetadata) {
			options.push(<option key={metadata.transientId} value={metadata.transientId}>{metadata.description}</option>);
		}
		return options;
	}

	return (<div>
		<select id={props.prefix + 'Selector'} value={selectedMetadata?.transientId} onChange={changeSelection}>
			{createItems()}
		</select>
		<button id={props.prefix + 'Select'} onClick={select}>Select</button>
	</div>
	);
}

