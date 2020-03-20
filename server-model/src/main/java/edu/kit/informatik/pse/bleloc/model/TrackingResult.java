package edu.kit.informatik.pse.bleloc.model;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * Represents an encrypted location encounter for a device
 */
@Entity
@Table(name = "trackingResult")
public class TrackingResult extends AbstractEntry{
	public static final Long InvalidId = null;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "encounteredAt", nullable = false)
	private Date encounteredAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="deviceId")
	private Device device;

	@Lob
	@Column(name = "data", nullable = false)
	private byte[] encryptedData;

	/**
	 * Zero-Argument constructor as required for Entity Beans
	 */
	public TrackingResult() {}


	/**
	 * Class constructor
	 * @param device tracked device
	 * @param encounteredAt date of encounter
	 * @param encryptedData encrypted data blob
	 */
	public TrackingResult(Device device, Date encounteredAt, byte[] encryptedData) {
		this.device = device;
		this.encounteredAt = encounteredAt;
		this.encryptedData = encryptedData;
	}

	/**
	 * Gets tracking result id
	 *
	 * @return the id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * Gets encounter date
	 *
	 * @return the encounter date
	 */
	public Date getEncounteredAt() {
		return this.encounteredAt;
	}

	/**
	 * Gets the associated device
	 *
	 * @return the associated device
	 */
	public Device getDevice() {
		return this.device;
	}

	/**
	 * Gets the associated user
	 *
	 * @return the associated user
	 */
	public UserAccount getUserAccount() {
		return this.device.getUserAccount();
	}

	/**
	 * Gets the encrypted data
	 *
	 * @return the encrypted data byte array
	 */
	public byte[] getEncryptedData() {
		return this.encryptedData;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		TrackingResult that = (TrackingResult) o;
		return Objects.equals(
			getId(), that.getId()) &&
		    getEncounteredAt().getTime() == that.getEncounteredAt().getTime() &&
	        Objects.equals(getDevice().getId(), that.getDevice().getId()) &&
	        Objects.equals(getUserAccount().getId(), that.getUserAccount().getId()) &&
	        Arrays.equals(getEncryptedData(), that.getEncryptedData());
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(getId(), getEncounteredAt().getTime(), getDevice().getId(), getUserAccount().getId());
		result = 31 * result + Arrays.hashCode(getEncryptedData());
		return result;
	}
}
