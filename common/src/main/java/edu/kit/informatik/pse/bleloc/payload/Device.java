package edu.kit.informatik.pse.bleloc.payload;

import edu.kit.informatik.pse.bleloc.model.HashedMacAddress;

/**
 * Contains data about a device as stored on the backend
 */
public class Device extends Payload {
	public static final int HardwareIdentifierByteLength = 16;

	private String hardwareIdentifier;

	public Device() {
	}

	public Device(HashedMacAddress hardwareIdentifier) {
		this.hardwareIdentifier = hardwareIdentifier.toString();
	}

	public String getHardwareIdentifier() {
		return hardwareIdentifier;
	}

	public void setHardwareIdentifier(String hardwareIdentifier) {
		this.hardwareIdentifier = hardwareIdentifier;
	}
}