package edu.kit.informatik.pse.bleloc.client.model.serialize;

import android.util.Log;
import androidx.annotation.NonNull;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kit.informatik.pse.bleloc.client.model.device.SynchronizableObject;
import edu.kit.informatik.pse.bleloc.client.model.user.UserData;
import edu.kit.informatik.pse.bleloc.client.model.utils.Encryptor;
import edu.kit.informatik.pse.bleloc.payload.UserDataPayload;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * Super class for converters that translate data from local objects to network transfer format and back.
 */
public abstract class UserDataSerializer {
	private static final int HeaderLength = 1;

	private final UserData userData;
	private final byte magicByte;

	public UserDataSerializer(@NonNull UserData userData, byte magicByte) {
		this.userData = userData;
		this.magicByte = magicByte;
	}

	/**
	 * Packs data from a local persistence object into a playload for network traffic.
	 *
	 * @param input
	 * 		The object to pack
	 * @return The packed payload
	 */
	public abstract UserDataPayload serialize(@NonNull SynchronizableObject input);

	/**
	 * Unpacks network traffic payload to a local persistence object.
	 *
	 * @param input
	 * 		The payload to unpack
	 * @return The local persistence object
	 */
	public abstract SynchronizableObject deserialize(@NonNull UserDataPayload input, SynchronizableObject target);

	protected byte[] encrypt(@NonNull Object object) {
		ByteArrayOutputStream result = null;

		try {
			ByteArrayOutputStream packedObject = new ByteArrayOutputStream();
			new ObjectMapper().writeValue(packedObject, object);
			final byte[] data = packedObject.toByteArray();

			Log.d("UserDataSerializer", "Encrypting object: " +  new String(data));

			final byte[] encryptedData = Encryptor.encrypt(userData.getLocalKey(), data);

			result = new ByteArrayOutputStream();
			result.write(magicByte);
			result.write(encryptedData, 0, encryptedData.length);
		} catch (Exception e) {
			Log.e("UserDataSerializer", "Failed to encrypt object: ", e);
		}

		return result != null ? result.toByteArray() : null;
	}

	protected boolean canDecrypt(@NonNull byte[] data) {
		return data.length > HeaderLength && data[0] == magicByte;
	}

	protected <T> T decrypt(@NonNull byte[] data, Class<T> type) {
		T result = null;

		if (canDecrypt(data)) {
			try {
				final byte[] packedObject =
					Encryptor.decrypt(userData.getLocalKey(), Arrays.copyOfRange(data, HeaderLength, data.length));

				Log.d("UserDataSerializer", "Decrypting object: " +  new String(packedObject));

				result = (new ObjectMapper().readValue(new ByteArrayInputStream(packedObject), type));
			} catch (Exception e) {
				Log.e("UserDataSerializer", "Failed to decrypt object: ", e);
			}
		}

		return result;
	}
}
