package edu.kit.informatik.pse.bleloc.client.controller;

import android.app.Application;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.room.Room;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.ScanResultDownloadManager;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.TrackingStateManager;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.UserDataSyncManager;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.requests.RequestManager;
import edu.kit.informatik.pse.bleloc.client.model.devicehashtable.DeviceHashTableStore;
import edu.kit.informatik.pse.bleloc.client.model.scanning.BackgroundScanManager;
import edu.kit.informatik.pse.bleloc.client.model.scanning.ScanResultUploadManager;
import edu.kit.informatik.pse.bleloc.client.model.scanning.Scanner;
import edu.kit.informatik.pse.bleloc.client.model.serialize.DeviceSerializer;
import edu.kit.informatik.pse.bleloc.client.model.serialize.LocationSerializer;
import edu.kit.informatik.pse.bleloc.client.model.serialize.UserDataSerializationManager;
import edu.kit.informatik.pse.bleloc.client.model.settings.Settings;
import edu.kit.informatik.pse.bleloc.client.model.user.*;

import java.io.File;

public class Context extends Application {
	private AppDatabase database;

	private RequestManager requestManager;
	private AuthenticationManager authenticationManager;
	private UserDataSerializationManager userDataSerializationManager;
	private UserDataSyncManager userDataSyncManager;
	private TrackingStateManager trackingStateManager;

	private UserData userData;
	private DeviceHashTableStore deviceHashTableStore;
	private Scanner scanner;
	private BackgroundScanManager backgroundScanManager;
	private LocationManager locationManager;
	private ScanResultUploadManager scanResultUploadManager;
	private ScanResultDownloadManager scanResultDownloadManager;
	private Settings settings;

	@Override
	public void onCreate() {
		super.onCreate();

		database = Room.databaseBuilder(this, AppDatabase.class, "blelocDB").allowMainThreadQueries().build();

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		settings = new Settings(sharedPreferences);
		userData = new UserData(sharedPreferences);

		scanner = new Scanner(((BluetoothManager) getSystemService(BLUETOOTH_SERVICE)).getAdapter());
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		requestManager = new RequestManager(userData);
		authenticationManager = new AuthenticationManager(userData);
		userDataSerializationManager = new UserDataSerializationManager();
		userDataSyncManager = new UserDataSyncManager(userDataSerializationManager);

		authenticationManager.setRequestManager(requestManager);

		userDataSerializationManager.registerStore(database.getDeviceStore(), new DeviceSerializer(userData));
		userDataSerializationManager
			.registerStore(database.getLocationStore(), new LocationSerializer(database.getDeviceStore(), userData));

		userDataSyncManager.setRequestManager(requestManager);
		userDataSyncManager.registerDataStore(database.getDeviceStore());
		userDataSyncManager.registerDataStore(database.getLocationStore());

		trackingStateManager = new TrackingStateManager(database.getDeviceTrackingStateStore(), database.getDeviceStore(), requestManager);
		userDataSyncManager.registerEventListener(trackingStateManager);

		scanResultDownloadManager = new ScanResultDownloadManager(database, requestManager, userDataSyncManager);
		userDataSyncManager.registerEventListener(scanResultDownloadManager);

		deviceHashTableStore = new DeviceHashTableStore(new File(getCacheDir(), "deviceHashTable"));
		scanResultUploadManager = new ScanResultUploadManager(database.getScanResultToUploadStore(), requestManager);
		backgroundScanManager = new BackgroundScanManager(scanner, deviceHashTableStore, requestManager, database.getScanResultToUploadStore(), locationManager, scanResultUploadManager);
		backgroundScanManager.setActive(true);
		scanner.attach(this);

		authenticationManager.registerListener((LoginEventListener) result -> {
			// Stop all background operations that operate on user data
			scanResultDownloadManager.cancel();
			userDataSyncManager.cancel();
			trackingStateManager.cancel();

			// Clear all local user data on log out
			getDatabase().getLocationStore().clear();
			getDatabase().getDeviceStore().clear();
			getDatabase().getDeviceTrackingStateStore().clear();
		});

		if (settings.isBackgroundScanningEnabled()
		    && (Build.VERSION.SDK_INT < 23
		        || (checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == PackageManager.PERMISSION_GRANTED
		            && checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED))) {
			if (Build.VERSION.SDK_INT >= 26) {
				startForegroundService(new Intent(this, ScanService.class));
			} else {
				startService(new Intent(this, ScanService.class));
			}
		}
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		scanner.detach(this);
	}

	public RequestManager getRequestManager() {
		return this.requestManager;
	}

	public AuthenticationManager getAuthenticationManager() {
		return this.authenticationManager;
	}

	public UserDataSyncManager getUserDataSyncManager() {
		return this.userDataSyncManager;
	}

	public TrackingStateManager getTrackingStateManager() {
		return this.trackingStateManager;
	}

	public AppDatabase getDatabase() {
		return this.database;
	}

	public DeviceHashTableStore getDeviceHashTableStore() {
		return this.deviceHashTableStore;
	}

	public Scanner getScanner() {
		return this.scanner;
	}

	public BackgroundScanManager getBackgroundScanManager() {
		return this.backgroundScanManager;
	}

	public ScanResultUploadManager getScanResultUploadManager() {
		return this.scanResultUploadManager;
	}

	public Settings getSettings() {
		return this.settings;
	}
}
