package edu.kit.informatik.pse.bleloc.model;

import javax.persistence.*;
import java.util.Objects;

/**
 * Represents an Administrator of the system.
 */
@Entity
@Table(name = "backofficeAccount")
public class BackofficeAccount extends AbstractEntry implements Authenticatable {
	public static final long InvalidId = -1;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "name", unique= true, nullable = false, length = 256)
	private String name;

	@Column(name = "passwordSalt", nullable = false, length = 256)
	private String passwordSalt;

	@Column(name = "hashedPassword", nullable = false, length = 256)
	private String hashedPassword;

	/**
	 * Construct a BackofficeAccount.
	 *
	 * @param name
	 * 		login name of the admin user
	 * @param password
	 * 		String containing password
	 */
	public BackofficeAccount(String name, String password) {
		this.name = name;
		this.passwordSalt = PasswordUtil.generateRandomSalt();
		this.setPassword(password);
	}

	/**
	 * Zero-Argument constructor as required for Entity Beans
	 */
	protected BackofficeAccount() {}

	/**
	 * Sets the hashed Password
	 * @param password
	 */
	private void setPassword(String password) {
		this.hashedPassword = PasswordUtil.hashPassword(password, this.passwordSalt);
	}

	@Override
	public Long getId() {
		return this.id;
	}

	/**
	 * Gets the user name
	 *
	 * @return the user name
	 */
	public java.lang.String getName() {
		return this.name;
	}

	@Override
	public boolean verifyPassword(java.lang.String password) {
		return (this.hashedPassword.equals(PasswordUtil.hashPassword(password, this.passwordSalt)));
	}

	@Override
	public void changePassword(java.lang.String newPassword) {
		changePassword(newPassword, PasswordUtil.generateRandomSalt());
	}

	/**
	 * Changes user password and salt
	 * @param newPassword new password
	 * @param passwordSalt new salt
	 */
	public void changePassword(String newPassword, String passwordSalt) {
		this.passwordSalt = passwordSalt;
		setPassword(newPassword);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		BackofficeAccount that = (BackofficeAccount) o;
		return Objects.equals(getId(), that.getId()) &&
		       Objects.equals(getName(), that.getName()) &&
		       Objects.equals(passwordSalt, that.passwordSalt) &&
		       Objects.equals(hashedPassword, that.hashedPassword);
	}
}
