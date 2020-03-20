package edu.kit.informatik.pse.bleloc.client.model.typeconverters;

import android.os.Build;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public final class MacAdressConverter {

	/**
	 * Converts a Mac Address String to a byte Array
	 * @param macAddress String in format "AA:BB:CC:DD:EE:FF"
	 * @return
	 */
	public static byte[] stringToByteArray(String macAddress) {
		String[] macAddressParts = macAddress.split(":");
		byte[] result = new byte[6];
		for(int i = 0; i < 6; i++) {
			Integer hexPart = Integer.parseInt(macAddressParts[i], 16);
			result[i] = hexPart.byteValue();
		}
		return result;
	}

	public static String byteArrayToString(byte[] macAddress) {
		StringBuilder sb = new StringBuilder(3 * macAddress.length - 1);
		sb.append(String.format("%02x", macAddress[0]));
		for (int i = 1; i < macAddress.length; i++) {
			sb.append(String.format(":%02x", macAddress[i]));
		}
		return sb.toString();
	}
}
