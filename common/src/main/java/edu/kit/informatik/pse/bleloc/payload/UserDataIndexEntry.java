package edu.kit.informatik.pse.bleloc.payload;

import java.util.Date;
import java.io.Serializable;
import java.util.Locale;

public class UserDataIndexEntry implements Serializable {
	private long id;
	private Date date;

	public UserDataIndexEntry() {
		// Empty default constructor as required by the Jackson JSON de/serializer library
	}

	public UserDataIndexEntry(long id, Date date) {
		this.id = id;
		this.date = date;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getModifiedAt() {
		return this.date;
	}

	public void setModifiedAt(Date date) {
		this.date = date;
	}

	@Override
	public boolean equals(Object other) {
		boolean result = false;

		if (other instanceof UserDataIndexEntry) {
			UserDataIndexEntry o = (UserDataIndexEntry) other;
			result = id == o.id && date.equals(o.date);
		}

		return result;
	}

	@Override
	public String toString() {
		return String.format(Locale.ENGLISH, "id: %d, date: %s", id, date.toGMTString());
	}
}
