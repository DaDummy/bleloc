package edu.kit.informatik.pse.bleloc.payload;

/**
 * Login credentials payload class
 */
public class LoginCredentialsPayload extends Payload {
	private String name;
	private String password;

	public LoginCredentialsPayload() {}

	public LoginCredentialsPayload(String name, String password) {
		this.name = name;
		this.password = password;
	}

	public String getName () {
		return this.name;
	}

	public void setName (String name) {
		this.name = name;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword (String password) {
		this.password = password;
	}
}
