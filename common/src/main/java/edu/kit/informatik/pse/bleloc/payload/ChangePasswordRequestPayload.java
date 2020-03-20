package edu.kit.informatik.pse.bleloc.payload;

/**
 * Payload for the change password request
 */
public class ChangePasswordRequestPayload extends Payload {
	private String oldPassword;
	private String newPassword;

	public ChangePasswordRequestPayload() {
	}

	public ChangePasswordRequestPayload(String oldPassword, String newPassword) {
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
	}

	public String getOldPassword() {
		return this.oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return this.newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
}
