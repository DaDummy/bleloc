package edu.kit.informatik.pse.bleloc.payload;


import java.util.Collection;

/**
 * Contains the list of devices that is sent to the client as a reaction to
 */
public class DeviceIndexPayload extends Payload {
	//public String hashedMacAddress;
	private Collection<String> index;


	public Collection<String> getIndex() {
		return this.index;
	}

	public void setIndex(Collection<String> index) {
		this.index = index;
	}
}
