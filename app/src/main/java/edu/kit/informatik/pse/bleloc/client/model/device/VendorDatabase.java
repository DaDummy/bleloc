package edu.kit.informatik.pse.bleloc.client.model.device;

import android.content.Context;
import edu.kit.informatik.pse.bleloc.client.model.typeconverters.MacAdressConverter;
import edu.kit.informatik.pse.bleloc.client.R;
import edu.kit.informatik.pse.bleloc.model.HashedMacAddress;

import java.io.*;
import java.util.Arrays;

/**
 * This class represents the vendor of the bluetooth device.
 */
public class VendorDatabase {

	/**
	 * Extracts the vendor information from a hardware identifier.
	 *
	 * @param hardwareIdentifier
	 * 		The hardware identifier to evaluate
	 * @return The vedor information
	 */
	public static String getVendorName(byte[] hardwareIdentifier, Context context) {
		String start = HashedMacAddress.fromByteArray(Arrays.copyOfRange(hardwareIdentifier, 0, 3)).toString().toUpperCase();

		InputStream is = context.getResources().openRawResource(R.raw.oui);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		try {
			String s = reader.readLine();
			while (s != null) {
				if (s.startsWith(start)) {
					return s.substring(6);
				}
				s = reader.readLine();
			}
		} catch (IOException e) {
			// fall
		}
		return null;
	}
}
