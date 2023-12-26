import React, { useContext } from 'react';
import { ApplicationStatusContext } from '../contexts/ApplicationStatusContext';

export function ApplicationStatusDisplay() {
	const applicationStatus = useContext(ApplicationStatusContext);
	return (<div id="application_status">
		<p className={"connected_" + applicationStatus.webSocketConnected}>WebSocket</p>
		<p className="error">{applicationStatus.lastError}</p>
	</div>);
}

