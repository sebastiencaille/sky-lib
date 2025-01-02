import React, {ReactNode, useCallback, useEffect, useState} from "react";
import {Metadata} from '../webapis/Types'

interface IMetadataChooserProps {
    prefix: string,
    allMetadata: Metadata[],
    initialySelectedMetadata?: Metadata,
    onSelection: (metatadata?: Metadata) => void
}

export default function MetadataChooser(props: Readonly<IMetadataChooserProps>) {

    const [selectedMetadata, setSelectedMetadata] = useState<Metadata | undefined>(undefined);

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

    const changeSelection = useCallback((e: React.ChangeEvent<HTMLSelectElement>) => {
        setSelectedMetadata(props.allMetadata.find((m) => (m.transientId === e.target.value)));
    }, [props.allMetadata]);

    const select = useCallback(() => {
        props.onSelection(selectedMetadata);
    }, [props, selectedMetadata]);

    const createItems = useCallback(() => {
        const options: ReactNode[] = [];
        if (!props.allMetadata) {
            return options;
        }
        for (const metadata of props.allMetadata) {
            options.push(<option key={metadata.transientId}
                                 value={metadata.transientId}>{metadata.description}</option>);
        }
        return options;
    }, [props.allMetadata]);

    return (<div>
            <select id={props.prefix + 'Selector'} value={selectedMetadata?.transientId} onChange={changeSelection}>
                {createItems()}
            </select>
            <button id={props.prefix + 'Select'} onClick={select} disabled={props.allMetadata.length === 0}>Select</button>
        </div>
    );
}

