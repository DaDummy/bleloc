package edu.kit.informatik.pse.bleloc.client.model.typeconverters;

import androidx.room.TypeConverter;
import edu.kit.informatik.pse.bleloc.model.HashedMacAddress;

public class HashedMacAddressConverter {

	@TypeConverter
	public static HashedMacAddress convertByteArrayToHashedMacAddress(byte[] array) {
		return HashedMacAddress.fromByteArray(array);
	}

	@TypeConverter
	public static byte[] convertHashedMacAddressToByteArray(HashedMacAddress hashedMacAddress) {
		return hashedMacAddress == null ? null : hashedMacAddress.toByteArray();
	}
}
