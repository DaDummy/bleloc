package edu.kit.informatik.pse.bleloc.payload;

import java.util.Date;

/**
 * A list of encrypted device tracking results as transferred between backend and client
 */
public class DeviceTrackingResultPayload extends Payload {
	private long trackingResultId;
	private Date date;
	private String hashedMacAddress;
	private byte[] encryptedData;


	public long getTrackingResultId() {
		return this.trackingResultId;
	}

	public void setTrackingResultId(long trackingResultId) {
		this.trackingResultId = trackingResultId;
	}

	public Date getEncounteredAt() {
		return this.date;
	}

	public void setEncounteredAt(Date date) {
		this.date = date;
	}

	public String getHashedHardwareIdentifier() {
		return this.hashedMacAddress;
	}

	public void setHashedHardwareIdentifier(String identifier) {
		this.hashedMacAddress = identifier;
	}

	public byte[] getEncryptedData() {
		return this.encryptedData;
	}

	public void setEncryptedData(byte[] data) {
		this.encryptedData = data;
	}
}
