package edu.kit.informatik.pse.bleloc.client.model.device;

/**
 * Enumeration of the states a bluetooth device may be in.
 */
public enum TrackingState {
	INACTIVE(0),
	TRACKED(1);

	private int code;

	private static TrackingState[] allValues = values();

	TrackingState(int n) {
		this.code = n;
	}

	public int getCode() {
		return code;
	}

	public static TrackingState fromInteger(int n) {
		if(n >= allValues.length || n < 0)
			return INACTIVE;
		return allValues[n];
	}
}
