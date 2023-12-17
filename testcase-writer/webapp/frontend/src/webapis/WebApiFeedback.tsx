import React from 'react';
import { StompSessionProvider, useSubscription } from "react-stomp-hooks";
import { StepStatus } from '../webapis/Types'


interface IWebApiFeedbackProps {
	stepStatusChanged: (status: StepStatus) => void;
};


interface StepStatusChanged {
	callback: (status: StepStatus) => void
}

function SubscribeTcFeedback(props: StepStatusChanged) {
	useSubscription("/feedback/testexec", (message) => props.callback(JSON.parse(message.body).payload as StepStatus));
	return (<div></div>)
}

class WebApiFeedback extends React.PureComponent<IWebApiFeedbackProps> {

	render() {
		return (
			<StompSessionProvider url= { "http://localhost:8080/api/websocket"} >
				<SubscribeTcFeedback callback={ this.props.stepStatusChanged } />
			</StompSessionProvider>
    	);
	}
}

export default WebApiFeedback;