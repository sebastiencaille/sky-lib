import React from 'react';
import { ErrorState } from '../service/Errors';

interface IApplicationStatus {
	webSocketConnected: boolean;
	errors: ErrorState;
}

class ApplicationStatusDisplay extends React.Component<IApplicationStatus, object> {

	render() {
		return (<div id="application_status"><p className={"connected_" + this.props.webSocketConnected}>WebSocket</p>
			<p className="error">{this.props.errors.lastError}</p></div>);
	}
}

export default ApplicationStatusDisplay;