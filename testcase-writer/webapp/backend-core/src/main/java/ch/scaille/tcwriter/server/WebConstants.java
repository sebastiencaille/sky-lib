package ch.scaille.tcwriter.server;

public class WebConstants {
		
	public static final String SPRING_SESSION_WEBSOCKET_SESSION = "WebSocketSession";
	
	private static final String USER_QUEUE = "/queue";

	public static final String TEST_EXECUTION_FEEDBACK = USER_QUEUE + "/testexec";

	private WebConstants() {
		// noop
	}

}
