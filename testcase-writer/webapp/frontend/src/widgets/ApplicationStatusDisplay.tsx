import React from 'react';
import { useApplicationStatusContext } from '../contexts/ApplicationStatusContext';

export function ApplicationStatusDisplay() {
	const applicationStatus = useApplicationStatusContext();
	return (<div id="application_status">
		<p className={"connected_" + applicationStatus.webSocketConnected}>WebSocket</p>
		<p className="error">{applicationStatus.lastError}</p>
	</div>);
}

