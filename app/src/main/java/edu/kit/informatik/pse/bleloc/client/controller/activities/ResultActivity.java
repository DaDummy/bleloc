package edu.kit.informatik.pse.bleloc.client.controller.activities;

import android.content.Intent;
import android.util.Xml;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.os.Bundle;
import edu.kit.informatik.pse.bleloc.client.R;
import edu.kit.informatik.pse.bleloc.client.controller.Context;
import edu.kit.informatik.pse.bleloc.client.model.device.Location;
import org.xmlpull.v1.XmlSerializer;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity {
	private ListView listView;
	private List<Location> locations;

	/**
	 * Activity for BT-Device's location Data.
	 *
	 * @param savedInstanceState
	 * 		to create Activity data.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		listView = findViewById(R.id.listView);

		long deviceId = getIntent().getLongExtra("deviceId", -1);
		locations = ((Context)getApplication()).getDatabase().getLocationStore().getByDevice(deviceId);
		String[] str = new String[locations.size()];
		int counter = 0;
		for (Location l : locations) {
			str[counter] = l.getDate().toString() + "\n" + l.getLongitude() + ", " + l.getLatitude() + "\nSignal strength: " + l.getSignalStrength();
			counter++;
		}

		listView.setAdapter(new ArrayAdapter<String>(this, R.layout.text_view, str));
	}

	private static final int REQUEST_EXPORT = 1;

	private void export() {
		Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("application/gpx+xml");
		intent.putExtra(Intent.EXTRA_TITLE, "export.gpx");
		startActivityForResult(intent, REQUEST_EXPORT);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_EXPORT && resultCode == RESULT_OK) {
			// TODO: move to its own class
			try (OutputStream os = getContentResolver().openOutputStream(data.getData())) {
				XmlSerializer serializer = Xml.newSerializer();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
				serializer.setOutput(new OutputStreamWriter(os));
				serializer.startDocument("UTF-8", true);
				serializer.startTag("", "gpx");
				serializer.attribute("", "version", "1.1");
				serializer.attribute("", "creator", "BLELoc");
				for (Location l : locations) {
					serializer.startTag("", "wpt");
					serializer.attribute("", "lat", "" + l.getLatitude());
					serializer.attribute("", "lon", "" + l.getLongitude());
					serializer.startTag("", "time");
					serializer.text(df.format(l.getDate()));
					serializer.endTag("", "time");
					serializer.startTag("", "cmt");
					serializer.text("Signal strength: " + l.getSignalStrength());
					serializer.endTag("", "cmt");
					serializer.endTag("", "wpt");
				}
				serializer.endTag("", "gpx");
				serializer.endDocument();
			} catch (IOException e) {
				Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_result, menu);
		menu.findItem(R.id.item_export).setVisible(android.os.Build.VERSION.SDK_INT >= 19);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.item_export:
				export();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
