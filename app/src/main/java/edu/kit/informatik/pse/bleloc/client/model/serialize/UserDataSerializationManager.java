package edu.kit.informatik.pse.bleloc.client.model.serialize;

import android.util.Pair;
import androidx.annotation.NonNull;
import edu.kit.informatik.pse.bleloc.client.model.device.SynchronizableObject;
import edu.kit.informatik.pse.bleloc.client.model.device.SynchronizableStore;
import edu.kit.informatik.pse.bleloc.payload.UserDataPayload;

import java.util.ArrayList;

/**
 * Holds references to store objects to serialize data from and back to them.
 */
public class UserDataSerializationManager {
	private ArrayList<Pair<UserDataSerializer, SynchronizableStore>> storeSerializers = new ArrayList<>();

	/**
	 * Regsiters a store and a corresponding serializer for its content.<br>
	 * <code>null</code> inputs on either argument result in ignoring the call
	 *
	 * @param synchronizableStore
	 * 		The store to be referenced
	 * @param serializer
	 * 		The serializer for the stores content
	 */
	public void registerStore(SynchronizableStore synchronizableStore, UserDataSerializer serializer) {
		if (synchronizableStore != null && serializer != null) {
			storeSerializers.add(Pair.create(serializer, synchronizableStore));
		}
	}

	public UserDataPayload serialize(@NonNull SynchronizableObject input) {
		UserDataPayload result = null;

		for (Pair<UserDataSerializer, SynchronizableStore> pair : storeSerializers) {
			result = pair.first.serialize(input);
			if (result != null) {
				break;
			}
		}

		return result;
	}

	public SynchronizableObject deserialize(@NonNull UserDataPayload input, SynchronizableObject target) {
		SynchronizableObject result = null;

		for (Pair<UserDataSerializer, SynchronizableStore> pair : storeSerializers) {
			result = pair.first.deserialize(input, target);
			if (result != null) {
				result.setStore(pair.second);
				break;
			}
		}

		return result;
	}
}
