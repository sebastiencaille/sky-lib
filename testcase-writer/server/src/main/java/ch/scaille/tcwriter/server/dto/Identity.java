package ch.scaille.tcwriter.server.dto;

import javax.servlet.http.HttpServletRequest;

public class Identity {

	private final String id;

	public Identity(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Identity && id.equals(((Identity) obj).id));
	}

	public static Identity of(HttpServletRequest request) {
		return new Identity(request.getSession().getId());
	}

	@Override
	public String toString() {
		return id;
	}
}
