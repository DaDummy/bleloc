package edu.kit.informatik.pse.bleloc.client.model.connectivity;

import android.util.Log;
import android.util.Pair;
import androidx.annotation.NonNull;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.*;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.requests.*;
import edu.kit.informatik.pse.bleloc.client.model.device.SynchronizableObject;
import edu.kit.informatik.pse.bleloc.client.model.device.SynchronizableStore;
import edu.kit.informatik.pse.bleloc.client.model.serialize.UserDataSerializationManager;
import edu.kit.informatik.pse.bleloc.payload.UserDataIndexEntry;
import edu.kit.informatik.pse.bleloc.payload.UserDataIndexPayload;
import edu.kit.informatik.pse.bleloc.payload.UserDataPayload;

import java.util.*;

/**
 * Manages the synchronization of local data with the server state.
 */
public class UserDataSyncManager
	implements UserDataSyncIndexResultListener, UserDataSyncAddResultListener, UserDataSyncGetResultListener,
	           UserDataSyncUpdateResultListener {
	private List<UserDataSyncEventListener> listeners = new ArrayList<>();
	private List<SynchronizableStore> dataStores = new ArrayList<>();
	private UserDataSerializationManager serializationManager;
	private RequestManager requestManager;
	private boolean isActive = false;
	private boolean isPendingCancel = false;
	private Queue<Long> downloadNewQueue = new ArrayDeque<>();
	private Queue<SynchronizableObject> downloadChangedQueue = new ArrayDeque<>();
	private Queue<SynchronizableObject> uploadChangedQueue = new ArrayDeque<>();
	private Queue<SynchronizableObject> uploadNewQueue = new ArrayDeque<>();
	private Queue<Pair<UserDataPayload, SynchronizableObject>> delayedDeserializationQueue = new ArrayDeque<>();

	public UserDataSyncManager(UserDataSerializationManager userDataSerializationManager) {
		this.serializationManager = userDataSerializationManager;
	}

	/**
	 * Synchronizes local data with the server state.
	 */
	public synchronized void triggerSync() {
		if (!isActive()) {
			isActive = true;

			Log.d("UserDataSyncManager", "Sync started");

			for (UserDataSyncEventListener listener : listeners) {
				listener.onPreSync();
			}

			UserDataSyncIndexRequest userDataSyncIndexRequest = new UserDataSyncIndexRequest();
			userDataSyncIndexRequest.registerListener(this);
			requestManager.send(userDataSyncIndexRequest);
		} else {
			Log.e("UserDataSyncManager","Sync triggered, but sync is already active!");
		}
	}

	private synchronized void endSync(boolean isSuccess) {
		if (!isActive()) {
			throw new IllegalStateException("endSync called, but sync is not active");
		}

		downloadNewQueue.clear();
		downloadChangedQueue.clear();
		uploadChangedQueue.clear();
		uploadNewQueue.clear();
		delayedDeserializationQueue.clear();

		isPendingCancel = false;
		isActive = false;

		if (isSuccess) {
			Log.d("UserDataSyncManager", "Sync ended");

			for (UserDataSyncEventListener listener : listeners) {
				listener.onPostSync();
			}
		} else {
			Log.d("UserDataSyncManager", "Sync canceled");
		}
	}


	public boolean isActive() {
		return isActive;
	}

	public void cancel() {
		if (isActive()) {
			isPendingCancel = true;
		}
	}

	public boolean isPendingCancel() {
		return isPendingCancel;
	}

	public synchronized void registerEventListener(@NonNull UserDataSyncEventListener listener) {
		Objects.requireNonNull(listener);
		listeners.add(listener);
	}

	public synchronized void unregisterEventListener(UserDataSyncEventListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Registers a store that shall be synchronized with the server.
	 *
	 * @param store
	 * 		The store to be synchronized
	 */
	public void registerDataStore(@NonNull SynchronizableStore store) {
		Objects.requireNonNull(store);
		if (dataStores.contains(store)) {
			throw new IllegalArgumentException("Tried to register a data store that is already registered");
		}
		dataStores.add(store);
	}

	public void setRequestManager(@NonNull RequestManager requestManager) {
		Objects.requireNonNull(requestManager);
		this.requestManager = requestManager;
	}

	@Override
	public void onReceiveUserDataSyncIndex(UserDataIndexPayload payload) {
		if (payload != null) {
			Log.d("UserDataSyncManager", "Index received containing " + payload.getIndex().size() + " entries");

			List<UserDataIndexEntry> remoteIndex = new ArrayList<>(payload.getIndex());

			// Build local index and upload new queue
			List<UserDataIndexEntry> localIndex = new ArrayList<>();
			Map<Long, SynchronizableObject> localObjectLookupMap = new TreeMap<>();
			for (SynchronizableStore dataStore : dataStores) {
				List<SynchronizableObject> syncIndex = dataStore.getSyncIndex();
				for (SynchronizableObject entry : syncIndex) {
					// TODO(ca): This doesn't really seem to be the right place for this
					entry.setStore(dataStore);

					final long syncId = entry.getSyncId();
					if (syncId == SynchronizableObject.InvalidSyncId) {
						uploadNewQueue.add(entry);
					} else {
						localIndex.add(new UserDataIndexEntry(syncId, entry.getSyncTimestamp()));
						localObjectLookupMap.put(syncId, entry);
					}
				}
			}

			// Sort indices by id for faster comparison
			Collections.sort(remoteIndex, (o1, o2) -> Long.compare(o1.getId(), o2.getId()));
			Collections.sort(localIndex, (o1, o2) -> Long.compare(o1.getId(), o2.getId()));

			// Fill queues
			Iterator<UserDataIndexEntry> remoteIterator = remoteIndex.iterator();
			Iterator<UserDataIndexEntry> localIterator = localIndex.iterator();

			UserDataIndexEntry remoteEntry = remoteIterator.hasNext() ? remoteIterator.next() : null;
			while (localIterator.hasNext()) {
				UserDataIndexEntry localEntry = localIterator.next();

				while (remoteEntry != null && remoteEntry.getId() < localEntry.getId()) {
					// Remote entry does not exist locally -> download
					downloadNewQueue.add(remoteEntry.getId());
					remoteEntry = remoteIterator.hasNext() ? remoteIterator.next() : null;
				}

				if (remoteEntry != null && remoteEntry.getId() == localEntry.getId()) {
					final int compareResult = remoteEntry.getModifiedAt().compareTo(localEntry.getModifiedAt());
					if (compareResult < 0) {
						// Local version is newer -> upload changed
						SynchronizableObject synchronizableObject = localObjectLookupMap.get(localEntry.getId());
						uploadChangedQueue.add(synchronizableObject);
					} else if (compareResult > 0) {
						// Remote version is newer -> download
						SynchronizableObject synchronizableObject = localObjectLookupMap.get(localEntry.getId());
						downloadChangedQueue.add(synchronizableObject);
					}

					remoteEntry = remoteIterator.hasNext() ? remoteIterator.next() : null;
				} else {
					// Local entry has a valid sync id, but does not exist remotely -> bug
					throw new IllegalStateException(
						"Local SynchronizableObject has a valid sync id, but does not exist remotely");
				}
			}

			while (remoteEntry != null) {
				// Remote entry does not exist locally -> download
				downloadNewQueue.add(remoteEntry.getId());
				remoteEntry = remoteIterator.hasNext() ? remoteIterator.next() : null;
			}

			remoteIndex.clear();
			localIndex.clear();
			localObjectLookupMap.clear();

			triggerNextRequest();
		} else {
			Log.e("UserDataSyncManager", "Failed to retrieve remote index");
			endSync(false);
		}
	}

	@Override
	public void onReceiveUserDataSyncAddResult(UserDataPayload payload, SynchronizableObject localObject) {
		Log.d("UserDataSyncManager", "Add result received");

		if (payload != null) {
			processAndTriggerNextRequest(payload, localObject);
		} else {
			Log.e("UserDataSyncManager", "Failed to upload new object to backend");
			endSync(false);
		}
	}

	@Override
	public void onReceiveUserDataSyncGetResult(UserDataPayload payload, SynchronizableObject localObject) {
		Log.d("UserDataSyncManager", "Data received");

		if (payload != null) {
			processAndTriggerNextRequest(payload, localObject);
		} else {
			Log.e("UserDataSyncManager", "Failed to download object");
			endSync(false);
		}
	}

	@Override
	public void onReceiveUserDataSyncUpdateResult(UserDataPayload payload, SynchronizableObject localObject) {
		Log.d("UserDataSyncManager", "Update result received");

		if (payload != null) {
			processAndTriggerNextRequest(payload, localObject);
		} else {
			Log.e("UserDataSyncManager", "Failed to update object on backend");
			endSync(false);
		}
	}

	private void processAndTriggerNextRequest(UserDataPayload receivedPayload, SynchronizableObject localObject) {
		updateOrAddLocalSynchonizableObjectFromPayload(receivedPayload, localObject);

		triggerNextRequest();
	}

	private void triggerNextRequest() {
		if (isPendingCancel()) {
			endSync(false);
		} else if (!downloadNewQueue.isEmpty()) {
			final long syncId = downloadNewQueue.remove();

			UserDataSyncGetRequest request = new UserDataSyncGetRequest(syncId);
			request.registerListener(this);
			requestManager.send(request);
		} else if (!downloadChangedQueue.isEmpty()) {
			final SynchronizableObject changedObject = downloadChangedQueue.remove();

			UserDataSyncGetRequest request = new UserDataSyncGetRequest(changedObject.getSyncId(), changedObject);
			request.registerListener(this);
			requestManager.send(request);
		} else if (!uploadChangedQueue.isEmpty()) {
			final SynchronizableObject changedObject = uploadChangedQueue.remove();
			UserDataPayload payload;

			if (changedObject.isDeleted()) {
				payload = new UserDataPayload();
				payload.setSyncId(changedObject.getSyncId());
				payload.setModifiedAt(changedObject.getSyncTimestamp());
				payload.setEncryptedData(new byte[0]);
			} else {
				payload = serializationManager.serialize(changedObject);
			}

			UserDataSyncUpdateRequest request = new UserDataSyncUpdateRequest(payload, changedObject);
			request.registerListener(this);
			requestManager.send(request);
		} else if (!uploadNewQueue.isEmpty()) {
			final SynchronizableObject newObject = uploadNewQueue.remove();
			final UserDataPayload payload = serializationManager.serialize(newObject);

			UserDataSyncAddRequest request = new UserDataSyncAddRequest(payload, newObject);
			request.registerListener(this);
			requestManager.send(request);
		} else if (!delayedDeserializationQueue.isEmpty()) {
			boolean madeProgress;

			do {
				madeProgress = false;

				for (Pair<UserDataPayload, SynchronizableObject> pair : delayedDeserializationQueue) {
					madeProgress |= updateOrAddLocalSynchonizableObjectFromPayload(pair.first, pair.second);
				}
			} while (madeProgress);

			if (!delayedDeserializationQueue.isEmpty()) {
				Log.e("UserDataSyncManager", "Failed to deserialize received object");
				endSync(false);
			}
		} else {
			endSync(true);
		}
	}

	private boolean updateOrAddLocalSynchonizableObjectFromPayload(UserDataPayload payload,
	                                                               SynchronizableObject localObject) {
		boolean wasProcessed = false;

		if (payload.getEncryptedData().length > 0) {
			SynchronizableObject payloadObject = serializationManager.deserialize(payload, localObject);
			if (payloadObject != null) {
				if (localObject != null) {
					// Update the existing object
					payloadObject.getStore().syncUpdate(payloadObject);
					wasProcessed = true;
				} else {
					// The object has been freshly downloaded - just add it
					payloadObject.getStore().add(payloadObject);
					wasProcessed = true;
				}
			} else {
				delayedDeserializationQueue.add(Pair.create(payload, localObject));
			}
		} else if (localObject != null) {
			// Incoming delete - simply remove the object in question
			localObject.getStore().syncDelete(localObject);
			wasProcessed = true;
		}

		return wasProcessed;
	}
}
