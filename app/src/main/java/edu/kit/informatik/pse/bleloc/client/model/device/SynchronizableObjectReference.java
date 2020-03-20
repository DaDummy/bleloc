package edu.kit.informatik.pse.bleloc.client.model.device;

import java.util.Date;

public class SynchronizableObjectReference {
	private long syncId;
	private Date syncTimestamp;
	private SynchronizableStore store;

	public SynchronizableObjectReference(long syncId, Date syncTimestamp, SynchronizableStore store) {
		throw new UnsupportedOperationException();
	}

	public Date getSyncTimestamp() {
		return this.syncTimestamp;
	}

	public long getSyncId() {
		return this.syncId;
	}

	public SynchronizableStore getStore() {
		return this.store;
	}
}
