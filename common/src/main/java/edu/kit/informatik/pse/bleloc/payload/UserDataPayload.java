package edu.kit.informatik.pse.bleloc.payload;

import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * Contains a single user sync data entry
 */
public class UserDataPayload extends Payload {
	private long id;
	private Date date;
	private byte[] encryptedData;

	public long getSyncId() {
		return this.id;
	}

	public void setSyncId(long id) {
		this.id = id;
	}

	public Date getModifiedAt() {
		return this.date;
	}

	public void setModifiedAt(Date date) {
		this.date = date;
	}

	public byte[] getEncryptedData() {
		return this.encryptedData;
	}

	public void setEncryptedData(byte[] data) {
		this.encryptedData = data;
	}

	@Override
	public boolean equals(Object other) {
		boolean result = false;

		if (other instanceof UserDataPayload) {
			UserDataPayload o = (UserDataPayload) other;
			result = id == o.id && date.equals(o.date) && Arrays.equals(encryptedData, o.encryptedData);
		}

		return result;
	}

	@Override
	public String toString() {
		return String.format(Locale.ENGLISH,"id: %d, date: %s, encryptedData: %s", id, date.toGMTString(), Arrays.toString(encryptedData));
	}
}
