package edu.kit.informatik.pse.bleloc.client.model.serialize;

import androidx.annotation.NonNull;
import edu.kit.informatik.pse.bleloc.client.model.device.Device;
import edu.kit.informatik.pse.bleloc.client.model.device.SynchronizableObject;
import edu.kit.informatik.pse.bleloc.client.model.user.UserData;
import edu.kit.informatik.pse.bleloc.payload.UserDataPayload;

import java.util.Objects;

public class DeviceSerializer extends UserDataSerializer {
	public static final byte MagicByte = 48;

	public DeviceSerializer(UserData userData) {
		super(userData, MagicByte);
	}

	@Override
	public UserDataPayload serialize(@NonNull SynchronizableObject input) {
		Objects.requireNonNull(input);

		UserDataPayload payload = null;

		if (input instanceof Device) {
			final PortableDevice portableDevice = packDevice((Device) input);

			final byte[] encryptedData = encrypt(portableDevice);

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

		Device device = null;

		final byte[] encryptedData = input.getEncryptedData();

		if (canDecrypt(encryptedData)) {
			final PortableDevice portableDevice = decrypt(encryptedData, PortableDevice.class);

			if (portableDevice != null) {
				if (target instanceof Device) {
					device = (Device) target;
				}

				device = unpackDevice(portableDevice, device);
				device.setSyncId(input.getSyncId());
				device.setSyncTimestamp(input.getModifiedAt());
			}
		}

		return device;
	}

	private @NonNull
	PortableDevice packDevice(@NonNull Device device) {
		Objects.requireNonNull(device);

		PortableDevice portableDevice = new PortableDevice();
		portableDevice.hardwareIdentifier = device.getHardwareIdentifier();
		portableDevice.name = device.getName();
		portableDevice.alias = device.getAlias();

		return portableDevice;
	}

	private @NonNull
	Device unpackDevice(@NonNull PortableDevice portableDevice, Device target) {
		Objects.requireNonNull(portableDevice);

		if (target == null) {
			target = new Device(portableDevice.hardwareIdentifier);
		} else {
			target.setHardwareIdentifier(portableDevice.hardwareIdentifier);
		}
		target.setName(portableDevice.name);
		target.setAlias(portableDevice.alias);

		return target;
	}

	private static class PortableDevice {
		public byte[] hardwareIdentifier;
		public String name;
		public String alias;
	}
}
