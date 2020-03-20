package edu.kit.informatik.pse.bleloc.client.model.device;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import android.bluetooth.BluetoothDevice;
import edu.kit.informatik.pse.bleloc.client.model.typeconverters.MacAdressConverter;
import edu.kit.informatik.pse.bleloc.client.model.utils.Encryptor;
import edu.kit.informatik.pse.bleloc.model.HashedMacAddress;

/**
 * This class represents a bluetooth device entry.
 */
@Entity(tableName = "device", indices = {@Index(value = "hardwareIdentifier", unique = true)})
public class Device extends SynchronizableObject {

	private byte[] hardwareIdentifier;
	private String name;
	private String alias = null;

	/**
	 * Class Constructor.
	 *
	 * @param hardwareIdentifier
	 * 		the device's mac address
	 */
	public Device(byte[] hardwareIdentifier) {
		this.hardwareIdentifier = hardwareIdentifier;
	}

	/**
	 * Creates a project local Device instance from Android's Bluetooth API's BluetoothDevice.
	 *
	 * @param bluetoothDevice
	 * 		The BluetoothDevice to turn into a Device
	 */
	public Device(BluetoothDevice bluetoothDevice) {
		alias = bluetoothDevice.getName();
		name = bluetoothDevice.getName();
		hardwareIdentifier = MacAdressConverter.stringToByteArray(bluetoothDevice.getAddress());
	}

	/**
	 * Sets the device's hardware identifier.
	 * @param hardwareIdentifier The hardware identifier
	 */
	public void setHardwareIdentifier(byte[] hardwareIdentifier) {
		this.hardwareIdentifier = hardwareIdentifier;
	}

	/**
	 * Returns the device's hardware identifier.
	 *
	 * @return The hardware identifier
	 */
	public byte[] getHardwareIdentifier() {
		return hardwareIdentifier;
	}

	/**
	 * Generates a hashed mac address
	 *
	 * @return the HashedMacAddress to the device's own mac address
	 */
	public HashedMacAddress getHashedHardwareIdentifier() {
		return Encryptor.hashedMacAddressFromKey(Encryptor.keyFromAddress(hardwareIdentifier));
	}

	/**
	 * Sets the device alias. This is purely a label that is freely chosen by the user.
	 *
	 * @param alias
	 * 		String with the alias name
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * Gets the device's alias.
	 *
	 * @return String with the alias name
	 */
	public String getAlias() {
		return this.alias;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
