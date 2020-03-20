package edu.kit.informatik.pse.bleloc.client.controller.activities;

import android.content.Intent;
import android.os.*;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.bluetooth.BluetoothDevice;
import edu.kit.informatik.pse.bleloc.client.R;
import edu.kit.informatik.pse.bleloc.client.controller.Context;
import edu.kit.informatik.pse.bleloc.client.model.device.Device;
import edu.kit.informatik.pse.bleloc.client.model.typeconverters.MacAdressConverter;

import java.lang.ref.WeakReference;
import java.util.*;

public class AddDeviceActivity extends AppCompatActivity {
	private ListView listView;
	private List<BluetoothDevice> pairedDevices;
	private List<byte[]> usedIdentifiers;

	/**
	 * Creating Activity.
	 *
	 * @param savedInstanceState
	 * 		to create Activity data.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_device);
		listView = findViewById(R.id.listView);
		listView.setEmptyView(findViewById(R.id.emptyView));

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Device device = new Device(pairedDevices.get(position));
				long generatedId = ((Context)getApplication()).getDatabase().getDeviceStore().add(device);
				startActivity(new Intent(AddDeviceActivity.this, DeviceDetailsActivity.class).putExtra("deviceId", generatedId));
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		usedIdentifiers = ((Context) getApplication()).getDatabase().getDeviceStore().getHardwareIdentifiers();
		pairedDevices = ((Context)getApplication()).getScanner().getPairedDevices();
		Iterator<BluetoothDevice> iterator = pairedDevices.iterator();
		while (iterator.hasNext()) {
			byte[] ta = MacAdressConverter.stringToByteArray(iterator.next().getAddress());
			for (byte[] x : usedIdentifiers) {
				if (Arrays.equals(ta, x)) {
					iterator.remove();
					break;
				}
			}
		}

		String[] names = new String[pairedDevices.size()];
		int index = 0;
		for (BluetoothDevice dev : pairedDevices) {
			names[index] = dev.getName();
			index++;
		}

		listView.setAdapter(new ArrayAdapter<String>(this, R.layout.text_view, names));
	}
}
