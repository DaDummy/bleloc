package edu.kit.informatik.pse.bleloc.client.controller;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import edu.kit.informatik.pse.bleloc.client.model.device.*;
import edu.kit.informatik.pse.bleloc.client.model.scanning.ScanResultToUpload;
import edu.kit.informatik.pse.bleloc.client.model.scanning.ScanResultToUploadStore;

@Database(entities = {ScanResultToUpload.class, Location.class, Device.class, DeviceTrackingState.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
	public abstract ScanResultToUploadStore getScanResultToUploadStore();
	public abstract LocationStore getLocationStore();
	public abstract DeviceStore getDeviceStore();
	public abstract DeviceTrackingStateStore getDeviceTrackingStateStore();
}
