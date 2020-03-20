package edu.kit.informatik.pse.bleloc.client.model.serialize;

import androidx.annotation.NonNull;
import edu.kit.informatik.pse.bleloc.client.model.device.Device;
import edu.kit.informatik.pse.bleloc.client.model.device.DeviceStore;
import edu.kit.informatik.pse.bleloc.client.model.device.Location;
import edu.kit.informatik.pse.bleloc.client.model.device.SynchronizableObject;
import edu.kit.informatik.pse.bleloc.client.model.user.UserData;
import edu.kit.informatik.pse.bleloc.payload.UserDataPayload;

import java.util.Date;
import java.util.Objects;

public class LocationSerializer extends UserDataSerializer {
	public static final byte MagicByte = 49;
	private DeviceStore deviceStore;

	public LocationSerializer(DeviceStore deviceStore, UserData userData) {
		super(userData, MagicByte);
		this.deviceStore = deviceStore;
	}

	@Override
	public UserDataPayload serialize(@NonNull SynchronizableObject input) {
		Objects.requireNonNull(input);

		UserDataPayload payload = null;

		if (input instanceof Location) {
			final PortableLocation portableLocation = packLocation((Location) input);

			final byte[] encryptedData = encrypt(portableLocation);

			payload = new UserDataPayload();
			payload.setSyncId(input.getSyncId());
			payload.setModifiedAt(input.getSyncTimestamp());
			payload.setEncryptedData(encryptedData);
		}

		return payload;
	}

	@Override
	public SynchronizableObject deserialize(@NonNull UserDataPayload input, SynchronizableObject target) {
		Objects.requireNonNull(input);

		Location location = null;

		final byte[] encryptedData = input.getEncryptedData();

		if (canDecrypt(encryptedData)) {
			final PortableLocation portableLocation = decrypt(encryptedData, PortableLocation.class);

			if (portableLocation != null) {
				if (target instanceof Location) {
					location = (Location) target;
				}

				location = unpackLocation(portableLocation, location);
				location.setSyncId(input.getSyncId());
				location.setSyncTimestamp(input.getModifiedAt());
			}
		}

		return location;
	}

	private PortableLocation packLocation(@NonNull Location location) {
		Objects.requireNonNull(location);

		PortableLocation portableLocation = null;

		Device device = deviceStore.getDevice(location.getDeviceId());
		if (device != null && device.getSyncId() != Device.InvalidSyncId) {
			portableLocation = new PortableLocation();
			portableLocation.deviceSyncId = device.getSyncId();
			portableLocation.date = location.getDate();
			portableLocation.signalStrength = location.getSignalStrength();
			portableLocation.intLatitude = location.getIntLatitude();
			portableLocation.intLongitude = location.getIntLongitude();
			portableLocation.seen = location.isSeen();
		}

		return portableLocation;
	}

	private @NonNull
	Location unpackLocation(@NonNull PortableLocation portableLocation, Location target) {
		Objects.requireNonNull(portableLocation);

		Location location = null;

		Device device = deviceStore.getDeviceBySyncId(portableLocation.deviceSyncId);
		if (device != null) {
			if (target == null) {
				location = new Location();
			} else {
				location = target;
			}
			location.setDeviceId(device.getId());
			location.setDate(portableLocation.date);
			location.setSignalStrength(portableLocation.signalStrength);
			location.setIntLatitude(portableLocation.intLatitude);
			location.setIntLongitude(portableLocation.intLongitude);
			location.setSeen(portableLocation.seen);
		}

		return location;
	}

	private static class PortableLocation {
		public long deviceSyncId;
		public Date date;
		public int signalStrength;
		public int intLatitude;
		public int intLongitude;
		public boolean seen;
	}
}
