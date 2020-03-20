package edu.kit.informatik.pse.bleloc.model;

import javax.inject.Singleton;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


@Singleton
public class DeviceHashTableStore {
	ReadWriteLock lock = new ReentrantReadWriteLock();
	DeviceHashTable dht;

	/**
	 * Put a new DeviceHashTable into the store.
	 *
	 * @param table
	 */
	public void replace(DeviceHashTable table) {
		Lock w = lock.writeLock();
		try {
			w.lock();
			dht = table;
		} finally {
			w.unlock();
		}
	}

	/**
	 * Get the DeviceHashTable.
	 *
	 * @return the DeviceHashTable.
	 */
	public DeviceHashTable get() {
		Lock r = lock.readLock();
		DeviceHashTable retVal;
		try {
			r.lock();
			retVal = dht;
		} finally {
			r.unlock();
		}
		return retVal;
	}
}
