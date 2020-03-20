package edu.kit.informatik.pse.bleloc.model;

/**
 * Interface for various user types that can perform a login into the system
 */
public interface Authenticatable {
	/**
	 * Gets the user name
	 *
	 * @return user login name
	 */
	public String getName();

	/**
	 * Checks if a user with this credentials is in storage
	 *
	 * @param password
	 * 		user login name
	 */
	public boolean verifyPassword(String password);

	/**
	 * Changes user password
	 *
	 * @param newPassword new password
	 */
	public void changePassword(String newPassword);

	/**
	 * Gets the user id
	 */
	public Long getId();

}
