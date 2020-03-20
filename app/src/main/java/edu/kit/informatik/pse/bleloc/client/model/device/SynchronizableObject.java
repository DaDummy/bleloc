package edu.kit.informatik.pse.bleloc.client.model.device;

import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import edu.kit.informatik.pse.bleloc.client.model.typeconverters.DateConverter;

import java.util.Date;

/**
 * Super class for all objects that shall be able to be synchronized via server.
 */
public abstract class SynchronizableObject {
	public static final long InvalidId = 0;
	public static final long InvalidSyncId = -1;

	@PrimaryKey(autoGenerate = true)
	private long id = InvalidId;

	private long syncId = InvalidSyncId;

	@TypeConverters(DateConverter.class)
	private Date syncTimestamp = new Date();

	private boolean isDeleted = false;

	@Ignore
	private SynchronizableStore store;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getSyncId() {
		return syncId;
	}

	public void setSyncId(long syncId) {
		this.syncId = syncId;
	}

	public Date getSyncTimestamp() {
		return syncTimestamp;
	}

	public void setSyncTimestamp(Date syncTimestamp) {
		this.syncTimestamp = syncTimestamp;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean deleted) {
		isDeleted = deleted;
	}

	/**
	 * Updates the timestamp for the last change to the current time.
	 */
	public void dataChanged() {
		this.syncTimestamp = new Date();
	}

	/**
	 * Returns the store this object is referenced in.
	 *
	 * @return The store that contains this object
	 */
	public SynchronizableStore getStore() {
		return store;
	}

	public void setStore(SynchronizableStore store) {
		this.store = store;
	}

	/**
	 * Creates and returns a SynchronizableObjectReference from this object instance.
	 *
	 * @return A SynchronizableObjectReference from this instance
	 */
	public SynchronizableObjectReference getReference() {
		return new SynchronizableObjectReference(getSyncId(), getSyncTimestamp(), getStore());
	}
}
