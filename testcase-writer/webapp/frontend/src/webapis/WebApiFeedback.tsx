import React from 'react';
import { StompSessionProvider, useSubscription } from "react-stomp-hooks";
import { StepStatus } from '../webapis/Types'

interface IWebApiFeedbackProps {
	connected: (connected: boolean) => void;
	stepStatusChanged: (status: StepStatus) => void;
};

interface StepStatusChanged {
	callback: (status: StepStatus) => void
}

function SubscribeTcFeedback(props: Readonly<StepStatusChanged>) {
	useSubscription("/user/queue/testexec", (message) => props.callback(JSON.parse(message.body).payload as StepStatus));
	return (<div></div>)
}

class WebApiFeedback extends React.Component<IWebApiFeedbackProps> {

	constructor(props: IWebApiFeedbackProps) {
		super(props);
		this.connected = this.connected.bind(this);
		this.disconnected = this.disconnected.bind(this);
	}

	private connected(receipt: object) {
		console.log(receipt);
		this.props.connected(true);
	}

	private disconnected(receipt: object) {
		console.log(receipt);
		this.props.connected(false);
	}

	render() {
		return (
			<StompSessionProvider url={"http://localhost:8080/api/websocket"}
				onConnect={this.connected}
				onDisconnect={this.disconnected} >
				<SubscribeTcFeedback callback={this.props.stepStatusChanged} />
			</StompSessionProvider >
		);
	}
}

export default WebApiFeedback;