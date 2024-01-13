import React, { useCallback, useMemo } from 'react';
import { StompSessionProvider, useSubscription } from "react-stomp-hooks";
import { StepStatus } from '../webapis/Types'
import { updateWebConnection, useApplicationStatusContextUpdater } from '../contexts/ApplicationStatusContext';

interface StepStatusUpdaterType {
	updateStepStatus: (status: StepStatus) => void;
}


interface WebApiFeedbackProps extends StepStatusUpdaterType {
	tabId: string
}

function SubscribingComponent(stepStatusUpdater: Readonly<StepStatusUpdaterType>) {
	useSubscription("/user/queue/testexec", (message) => stepStatusUpdater.updateStepStatus((JSON.parse(message.body).payload) as StepStatus));
	return (<div></div>)
}


function WebApiFeedback(props: Readonly<WebApiFeedbackProps>) {

	const appStatusUpdate = useApplicationStatusContextUpdater();

	const headers = useMemo(() => ({ 'tabId': props.tabId }), [props.tabId]);

	const connect = useCallback(() => appStatusUpdate(updateWebConnection(true)), [appStatusUpdate]);

	const disconnect = useCallback(() => appStatusUpdate(updateWebConnection(false)), [appStatusUpdate]);

	return (
		<StompSessionProvider url={"/api/websocket"}
			connectHeaders={headers}
			onConnect={connect}
			onDisconnect={disconnect}
			onWebSocketClose={disconnect}
			>
			<SubscribingComponent updateStepStatus={props.updateStepStatus} />
		</StompSessionProvider >
	);
}
export default WebApiFeedback;