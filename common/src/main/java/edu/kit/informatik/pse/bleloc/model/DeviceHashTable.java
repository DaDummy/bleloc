package edu.kit.informatik.pse.bleloc.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

/**
 * Manages the BloomFilter Device Table
 */
public class DeviceHashTable {
	private Long id;

	private Date lastUpdate;

	private BloomFilter<byte[]> bloomFilter;

	private long insertionsLeft;

	/**
	 * Constructs an empty DeviceHashTable.
	 *
	 * @param maxElementCount
	 * 		required count of hardware identifiers to be added
	 */
	public DeviceHashTable(long maxElementCount) {
		lastUpdate = new Date();
		bloomFilter = BloomFilter.create(Funnels.byteArrayFunnel(), maxElementCount, 0.005);
		insertionsLeft = maxElementCount;
	}

	/**
	 * Reconstructs a DeviceHashTable.
	 *
	 * @param modifiedAt
	 * 		the time of the last update
	 * @param serializedHashTable
	 * 		the byte array containing the DeviceHashTable contents
	 */
	public DeviceHashTable(Date modifiedAt, byte[] serializedHashTable) throws IOException {
		lastUpdate = modifiedAt;
		bloomFilter = BloomFilter.readFrom(new ByteArrayInputStream(serializedHashTable), Funnels.byteArrayFunnel());
		insertionsLeft = 0;
	}

	/**
	 * Try to add a hardware identifier. This may fail if maxElementCount is exceeded.
	 *
	 * @param identifier
	 * 		the hardware identifier to be added
	 * @return true if add was possible, false otherwise
	 */
	public boolean add(HashedMacAddress identifier) {
		if (insertionsLeft > 0) {
			lastUpdate = new Date();
			insertionsLeft--;
			bloomFilter.put(identifier.toByteArray());
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Test if a hardware identifier was added. False positives are possible.
	 *
	 * @param identifier
	 * @return false if the identifier is definitively not added, true otherwise
	 */
	public boolean contains(HashedMacAddress identifier) {
		return bloomFilter.mightContain(identifier.toByteArray());
	}

	/**
	 * Gets the time the DeviceHashTable was last updated.
	 *
	 * @return the time of the last update
	 */
	public Date getLastUpdateTime() {
		return lastUpdate;
	}

	/**
	 * Serialize the DeviceHashTable to byte Array
	 *
	 * @return the byte array containing the DeviceHashTable contents
	 */
	public byte[] getSerialized() {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bloomFilter.writeTo(bos);
			return bos.toByteArray();
		} catch (IOException e) { // should never happen
			throw new RuntimeException(e);
		}
	}
}
