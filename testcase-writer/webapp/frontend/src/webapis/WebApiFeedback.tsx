import { useCallback, useMemo } from 'react';
import { StompSessionProvider, useSubscription } from "react-stomp-hooks";
import { StepStatus } from './Types'
import { updateWebConnection, useApplicationStatusContextUpdater } from '../contexts/ApplicationStatusContext';

interface StepStatusUpdaterType {
	updateStepStatus: (status: StepStatus) => void;
}


interface WebApiFeedbackProps extends StepStatusUpdaterType {
	enabled: boolean;
	tabId: string;
}

function SubscribingComponent(stepStatusUpdater: Readonly<StepStatusUpdaterType>) {
	useSubscription("/user/queue/testexec", (message) => stepStatusUpdater.updateStepStatus((JSON.parse(message.body).payload) as StepStatus));
	return (<div></div>)
}


function WebApiFeedback(props: Readonly<WebApiFeedbackProps>) {

	const appStatusUpdate = useApplicationStatusContextUpdater();

	const headers = useMemo(() => ({ 'tabId': props.tabId }), [props.tabId]);

	const connected = useCallback(() => appStatusUpdate(updateWebConnection(true)), [appStatusUpdate]);

	const disconnected = useCallback(() => appStatusUpdate(updateWebConnection(false)), [appStatusUpdate]);

	return (
		<StompSessionProvider url={"/api/v0/websocket"}
			enabled={props.enabled}
			connectHeaders={headers}
			onConnect={connected}
			onDisconnect={disconnected}
			onWebSocketClose={disconnected}
			>
			<SubscribingComponent updateStepStatus={props.updateStepStatus} />
		</StompSessionProvider >
	);
}
export default WebApiFeedback;