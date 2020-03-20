package edu.kit.informatik.pse.bleloc.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "userData", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"id", "userId"})
})
public class UserDataEntry extends AbstractEntry {
	public static final Long InvalidId = null;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name="userId")
	private UserAccount user;

	@Column(name="time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date time;

	@Lob
	@Column(name = "data", nullable = false)
	private byte[] data;

	/**
	 * Zero-Argument constructor as required for Entity Beans
	 */
	public UserDataEntry() {}


	/**
	 * Class constructor
	 * @param time
	 * @param data
	 * @param user
	 */
	public UserDataEntry(Date time, byte[] data, UserAccount user) {
		this.time = time;
		this.data = data;
		this.user = user;
	}

	/**
	 * Gets the id of the user data entry
	 *
	 * @return the id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * Gets the date at which the entry was modified at
	 *
	 * @return the date
	 */
	public Date getModifiedAt() {
		return this.time;
	}

	/**
	 * Sets the date at which the entry was modified at
	 *
	 * @param time
	 * 		the date
	 */
	public void setModifiedAt(Date time) {
		this.time = time;
	}

	/**
	 * Gets the encrypted data byte array
	 *
	 * @return the byte array
	 */
	public byte[] getEncryptedData() {
		return this.data;
	}

	/**
	 * Gets the id of the user data entry
	 *
	 * @return the id
	 */
	public void setEncryptedData(byte[] data) {
		this.data = data;
	}

	/**
	 * Gets the owner of the data
	 * @return UserAccount of the owner
	 */
	public  UserAccount getUser() {
		return this.user;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		UserDataEntry that = (UserDataEntry) o;
		return Objects.equals(getId(), that.getId()) && Objects.equals(getUser().getId(), that.getUser().getId()) &&
		       time.getTime() == time.getTime() && Arrays.equals(data, that.data);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(getId(), getUser().getId(), time);
		result = 31 * result + Arrays.hashCode(data);
		return result;
	}
}
