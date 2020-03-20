package edu.kit.informatik.pse.bleloc.payload;

import java.util.Date;

/**
 * Contains the current bloom filter and associated information
 */
public class DeviceHashTablePayload extends Payload {
    private Date date;
    private byte[] encryptedData;

	public Date getModifiedAt() {
		return this.date;
	}

	public void setModifiedAt(Date date) {
		this.date = date;
	}

	public byte[] getData() {
		return this.encryptedData;
	}

	public void setData(byte[] data) {
		this.encryptedData = data;
	}
}
