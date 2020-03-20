package edu.kit.informatik.pse.bleloc.payload;

import com.google.common.collect.Iterables;

import java.util.Collection;

/**
 * Contains an index over the user synchronization data currently present on the backend
 */
public class UserDataIndexPayload extends Payload {
	private Collection<UserDataIndexEntry> index;

	public Collection<UserDataIndexEntry> getIndex() {
		return this.index;
	}

	public void setIndex(Collection<UserDataIndexEntry> index) {
		this.index = index;
	}

	@Override
	public boolean equals(Object other) {
		boolean result = false;

		if (other instanceof UserDataIndexPayload) {
			UserDataIndexPayload o = (UserDataIndexPayload) other;
			result = Iterables.elementsEqual(index, o.index);
		}

		return result;
	}

	@Override
	public String toString() {
		return Iterables.toString(index);
	}
}
