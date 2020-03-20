package edu.kit.informatik.pse.bleloc.model;

public class DeviceHashTableManager implements DeviceStoreListener {
	private DeviceStore deviceStore;
	private DeviceHashTableStore deviceHashTableStore;

	/**
	 * Constructs a DeviceHashTableManager which registers listener put a DeviceHashTable into DeviceHashTableStore
	 * immediately.
	 *
	 * @param deviceStore
	 * 		the DeviceStore.
	 * @param deviceHashTableStore
	 * 		the DeviceHashTableStore into which the generated DeviceHashTables are put.
	 */
	public DeviceHashTableManager(DeviceStore deviceStore, DeviceHashTableStore deviceHashTableStore) {
		this.deviceStore = deviceStore;
		this.deviceHashTableStore = deviceHashTableStore;
	}

	/**
	 * Creates the DeviceHashTable using DeviceStore and puts it into the DeviceHashTableStore.
	 */
	public void recreateDeviceHashTable() {
		DeviceHashTable dht = new DeviceHashTable(deviceStore.getCount());
		for (HashedMacAddress address : deviceStore.getAllHardwareIdentifiers()) {
			dht.add(address);
		}
		deviceHashTableStore.replace(dht);
	}

	@Override
	public void onAddDevice(Device device) {
		recreateDeviceHashTable();
	}

	@Override
	public void onRemoveDevice(Device device) {
		recreateDeviceHashTable();
	}
}
