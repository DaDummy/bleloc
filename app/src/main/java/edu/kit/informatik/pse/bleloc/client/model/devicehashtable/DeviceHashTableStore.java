package edu.kit.informatik.pse.bleloc.client.model.devicehashtable;
import android.util.AtomicFile;
import edu.kit.informatik.pse.bleloc.model.DeviceHashTable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

/**
 * Manages the DeviceHashTable used to check for tracked devices.
 */
public class DeviceHashTableStore {

	private DeviceHashTable storedTable;
	private AtomicFile atomicFile;

	public DeviceHashTableStore(File f) {
		this.atomicFile = new AtomicFile(f);

		try {
			byte[] content = atomicFile.readFully();
			if(content.length <= 8){
				this.storedTable = new DeviceHashTable(0);
			}
			else {
				Date date = new Date(bytesToLong(Arrays.copyOfRange(content, 0, 8)));
				byte[] data = Arrays.copyOfRange(content, 8, content.length);
				this.storedTable = new DeviceHashTable(date, data);
			}
		} catch (IOException e) {
			// storedTable is null
		}
	}

	/**
	 * Replaces the current DeviceHashTable with a new one.<br>
	 * <code>null</code> inputs are ignored.
	 *
	 * @param table
	 * 		The new table to be stored
	 */
	public void replace(DeviceHashTable table) {
		if (table == null) {
			return;
		}

		try {
			FileOutputStream fos = atomicFile.startWrite();
			fos.write(longToBytes(table.getLastUpdateTime().getTime()));
			fos.write(table.getSerialized());
			atomicFile.finishWrite(fos);
			storedTable = table;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the current DeviceHashTable.
	 *
	 * @return The current DeviceHashTable
	 */
	public DeviceHashTable get() {
		return storedTable;
	}

	// from https://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java/29132118#29132118
	private static byte[] longToBytes(long l) {
		byte[] result = new byte[8];
		for (int i = 7; i >= 0; i--) {
			result[i] = (byte)(l & 0xFF);
			l >>= 8;
		}
		return result;
	}

	private static long bytesToLong(byte[] b) {
		long result = 0;
		for (int i = 0; i < 8; i++) {
			result <<= 8;
			result |= (b[i] & 0xFF);
		}
		return result;
	}
}
