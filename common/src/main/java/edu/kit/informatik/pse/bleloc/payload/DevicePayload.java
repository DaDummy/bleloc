package edu.kit.informatik.pse.bleloc.payload;


import java.util.Date;

/**
 * The data of the response to a register device request
 */
public class DevicePayload extends Payload {
	private String hashedMachAddress;
	private Date date;

	public String getHashedHardwareIdentifier() {
		return this.hashedMachAddress;
	}

	public void setHashedHardwareIdentifier(String identifier) {
		this.hashedMachAddress = identifier;
	}

	public Date getTrackUntil() {
		return this.date;
	}

	public void setTrackUntil(Date date) {
		this.date = date;
	}
}
