package edu.kit.informatik.pse.bleloc.client.controller.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import edu.kit.informatik.pse.bleloc.client.BuildConfig;
import edu.kit.informatik.pse.bleloc.client.R;
import edu.kit.informatik.pse.bleloc.client.controller.Context;
import edu.kit.informatik.pse.bleloc.client.model.device.Location;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import java.io.File;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MapActivity extends AppCompatActivity {
	/**
	 * Activity for BT-Device's location Data.
	 *
	 * @param savedInstanceState
	 * 		to create Activity data.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Configuration.getInstance().setOsmdroidTileCache(new File(getCacheDir().getAbsolutePath(), "osmdroid"));
		Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID + "/" + BuildConfig.VERSION_NAME + " " + System.getProperty("http.agent"));
		setContentView(R.layout.activity_map);
		MapView map = findViewById(R.id.mapview);
		map.setMultiTouchControls(true);
		map.setTilesScaledToDpi(true);
		map.getController().setZoom(5.);

		long deviceId = getIntent().getLongExtra("deviceId", -1);
		List<Location> locations = ((Context)getApplication()).getDatabase().getLocationStore().getByDevice(deviceId);
		GeoPoint gp = null;
		for (Location l : locations) {
			Marker marker = new Marker(map);
			marker.setPosition(gp = new GeoPoint(l.getLatitude(), l.getLongitude()));
			marker.setIcon(this.getResources().getDrawable(R.drawable.ic_marker));
			marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
			marker.setTitle(l.getDate().toString());
			map.getOverlays().add(marker);
		}
		if (gp != null) {
			map.getController().setCenter(gp);
		}
	}
}
