package edu.kit.informatik.pse.bleloc.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Represents a bluetooth device entry.
 */
@Entity
@Table(name = "device")
public class Device extends AbstractEntry{
	public static final Long InvalidId = null;

	@Id
	@GeneratedValue(strategy =  GenerationType.SEQUENCE)
	private Long id;

	@Column(name = "trackUntil")
	private Date trackUntil;

	@Column(name = "date")
	private Date date;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="userId")
	private UserAccount user;

	@Column(name = "hwId", length = edu.kit.informatik.pse.bleloc.payload.Device.HardwareIdentifierByteLength)
	private byte[] hardwareIdentifier;

	// Only here for JPA cascade delete support
	@OneToMany(mappedBy = "device", cascade = CascadeType.REMOVE)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<TrackingResult> trackingResults;


	private static int TRACKINGDURATION = 30;

	/**
	 * Zero-Argument constructor as required for Entity Beans
	 */
	public Device() {}

	/**
	 * Class Constructor.
	 *
	 * @param user
	 * 		the owner of the device
	 * @param hardwareIdentifier
	 * 		the hashed mac address of the device
	 */
	public Device(UserAccount user, HashedMacAddress hardwareIdentifier) {
		this.user = user;
		this.hardwareIdentifier = hardwareIdentifier.toByteArray();
		this.date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.date);
		calendar.add(Calendar.DATE, TRACKINGDURATION);
		this.trackUntil = calendar.getTime();
	}

	/**
	 * Gets the device id.
	 *
	 * @return the device id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * Gets the date and time at which point tracking should stop.
	 *
	 * @return the Date until which to continue tracking
	 */
	public Date getTrackUntil() {
		return this.trackUntil;
	}

	/**
	 * Sets a date and time at which point tracking should stop.
	 *
	 * @param date
	 * 		the Date until which to continue tracking
	 */
	public void setTrackUntil(Date date) {
		this.trackUntil = date;
	}

	/**
	 * Gets the associated user account
	 *
	 * @return the associated user account
	 */
	public UserAccount getUserAccount() {
		return this.user;
	}

	/**
	 * Gets the associated hardware identifier
	 *
	 * @return the associated hardware identifier
	 */
	public HashedMacAddress getHardwareIdentifier() {
		return HashedMacAddress.fromByteArray(this.hardwareIdentifier);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Device device = (Device) o;
		return Objects.equals(getId(), device.getId()) &&
		       date.getTime() == device.date.getTime() &&
		       Objects.equals(user.getId(), device.user.getId()) &&
		       Objects.equals(getHardwareIdentifier(), device.getHardwareIdentifier());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), date.getTime(), user, getHardwareIdentifier().toString());
	}
}
