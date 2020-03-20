package edu.kit.informatik.pse.bleloc.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

/**
 * Represents a user account entity.
 */
@Entity
@Table(name = "userAccount")
public class UserAccount extends AbstractEntry implements Authenticatable {
	public static final Long InvalidId = null;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "name", unique= true, nullable = false, length = 256)
	private String name;

	@Column(name = "passwordSalt", nullable = false, length = 256)
	private String passwordSalt;

	@Column(name = "hashedPassword", nullable = false, length = 256)
	private String hashedPassword;

	// Only here for JPA cascade delete support
	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<Device> devices;

	// Only here for JPA cascade delete support
	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<UserDataEntry> userDataEntries;

	/**
	 * Construct a UserAccount.
	 *
	 * @param name
	 * 		login name of the user
	 * @param password
	 * 		String containing password
	 */
	public UserAccount(String name, String password) {
		this.name = name;
		this.passwordSalt = PasswordUtil.generateRandomSalt();
		this.setPassword(password);
	}

	/**
	 * Zero-Argument constructor as required for Entity Beans
	 */
	protected UserAccount() {}

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
	 * @return String containing the user name
	 */
	public String getName() {
		return this.name;
	}

	@Override
	public boolean verifyPassword(String password) {
		return (this.hashedPassword.equals(PasswordUtil.hashPassword(password, this.passwordSalt)));
	}

	@Override
	public void changePassword(String newPassword){
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
		UserAccount that = (UserAccount) o;
		return Objects.equals(getId(), that.getId()) &&
		       Objects.equals(getName(), that.getName()) &&
		       Objects.equals(passwordSalt, that.passwordSalt) &&
		       Objects.equals(hashedPassword, that.hashedPassword);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getName(), passwordSalt, hashedPassword);
	}
}