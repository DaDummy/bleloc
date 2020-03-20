package edu.kit.informatik.pse.bleloc.client.model.connectivity;

import android.util.Log;
import android.util.Pair;
import androidx.annotation.NonNull;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.UserDataSyncEventListener;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.requests.*;
import edu.kit.informatik.pse.bleloc.client.model.device.SynchronizableObject;
import edu.kit.informatik.pse.bleloc.client.model.device.SynchronizableStore;
import edu.kit.informatik.pse.bleloc.client.model.serialize.UserDataSerializationManager;
import edu.kit.informatik.pse.bleloc.payload.UserDataIndexEntry;
import edu.kit.informatik.pse.bleloc.payload.UserDataIndexPayload;
import edu.kit.informatik.pse.bleloc.payload.UserDataPayload;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class UserDataSyncManagerTest {
	private List<UserDataSyncEventListener> listeners = new ArrayList<>();
	private List<SynchronizableStore> dataStores = new ArrayList<>();
	private RequestManager requestManager;
	private boolean isActive = false;
	private boolean isPendingCancel = false;
	private Queue<Long> downloadNewQueue = new ArrayDeque<>();
	private Queue<SynchronizableObject> downloadChangedQueue = new ArrayDeque<>();
	private Queue<SynchronizableObject> uploadChangedQueue = new ArrayDeque<>();
	private Queue<SynchronizableObject> uploadNewQueue = new ArrayDeque<>();
	private Queue<Pair<UserDataPayload, SynchronizableObject>> delayedDeserializationQueue = new ArrayDeque<>();

	private UserDataSyncManager userDataSyncManager;
	private UserDataSerializationManager serializationManager;

	@Before
	public void setUp() {
		this.userDataSyncManager = new UserDataSyncManager(serializationManager);
	}

	@After
	public void tearDown() {
		this.userDataSyncManager = null;
	}

	@Test
	public void triggerSync() {
		this.userDataSyncManager.triggerSync();
	}

	@Test
	public void isActive() {
		this.userDataSyncManager.isActive();
	}

	@Test
	public void cancel() {
		this.userDataSyncManager.cancel();
	}

	@Test
	public void isPendingCancel() {
		this.userDataSyncManager.isPendingCancel();
	}

	@Test
	public void registerEventListener() {
		this.userDataSyncManager.registerEventListener(null);
	}

	@Test
	public void unregisterEventListener() {
		this.userDataSyncManager.unregisterEventListener(null);
	}

	@Test
	public void registerDataStore() {
		this.userDataSyncManager.registerDataStore(null);
	}

	@Test
	public void setRequestManager() {
		this.userDataSyncManager.setRequestManager(null);
	}

	@Test
	public void onReceiveUserDataSyncIndex() {
		this.userDataSyncManager.onReceiveUserDataSyncIndex(null);
	}

	@Test
	public void onReceiveUserDataSyncAddResult() {
		this.userDataSyncManager.onReceiveUserDataSyncAddResult(null, null);
	}

	@Test
	public void onReceiveUserDataSyncGetResult() {
		this.userDataSyncManager.onReceiveUserDataSyncGetResult(null, null);
	}

	@Test
	public void onReceiveUserDataSyncUpdateResult() {
		this.userDataSyncManager.onReceiveUserDataSyncUpdateResult(null, null);
	}

}
