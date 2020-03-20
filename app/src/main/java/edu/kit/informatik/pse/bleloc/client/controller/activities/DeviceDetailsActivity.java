package edu.kit.informatik.pse.bleloc.client.controller.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import edu.kit.informatik.pse.bleloc.client.R;
import edu.kit.informatik.pse.bleloc.client.controller.Context;
import edu.kit.informatik.pse.bleloc.client.model.device.*;
import edu.kit.informatik.pse.bleloc.client.model.typeconverters.MacAdressConverter;
import edu.kit.informatik.pse.bleloc.model.HashedMacAddress;

import java.util.Date;
import java.util.List;

public class DeviceDetailsActivity extends AppCompatActivity {

	private TextView textAlias, textName, textDeviceType, textVendor, textMacAddress;
	private Switch switchToggle;
	private Device device;

	/**
	 * Activity to view the details of the registered BT-Device.
	 *
	 * @param savedInstanceState
	 * 		to create Activity data.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_details);
		textAlias = findViewById(R.id.textAlias);
		textName = findViewById(R.id.textName);
		textDeviceType = findViewById(R.id.textDeviceType);
		textVendor = findViewById(R.id.textVendor);
		textMacAddress = findViewById(R.id.textMacAddress);
		switchToggle = findViewById(R.id.toggle);

		Device predevice;

		Intent intent = getIntent();
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			List<String> parts = intent.getData().getPathSegments();
			byte[] hwid = HashedMacAddress.fromString(parts.get(0)).toByteArray();
			predevice = ((Context) getApplication()).getDatabase().getDeviceStore().getDevice(hwid);
			if (predevice == null) {
				predevice = new Device(hwid);
				predevice.setName(parts.get(1));
				if (parts.size() >= 3) {
					predevice.setAlias(parts.get(2));
				} else {
					predevice.setAlias(parts.get(1));
				}
				((Context) getApplication()).getDatabase().getDeviceStore().add(predevice);
			}
		} else {
			predevice = ((Context) getApplication()).getDatabase().getDeviceStore().getDevice(intent.getLongExtra("deviceId", -1));
			switchToggle.setChecked(((Context) getApplication()).getTrackingStateManager().isTracked(predevice));
		}

		device = predevice;
		textAlias.setText(device.getAlias());
		textName.setText(device.getName());
		textVendor.setText(VendorDatabase.getVendorName(device.getHardwareIdentifier(), this));
		textMacAddress.setText(MacAdressConverter.byteArrayToString(device.getHardwareIdentifier()));

		findViewById(R.id.itemAlias).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final EditText input = new EditText(DeviceDetailsActivity.this);
				input.setText(device.getAlias());
				new AlertDialog.Builder(DeviceDetailsActivity.this)
					.setTitle("Change Alias")
					.setView(input)
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							device.setAlias(input.getText().toString());
							((Context)getApplication()).getDatabase().getDeviceStore().update(device);
							textAlias.setText(device.getAlias());
						}
					})
					.setNegativeButton("Cancel", null)
					.show();
			}
		});

		switchToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				((Context) getApplication()).getTrackingStateManager().toggleDeviceTrackingStatus(device, isChecked);
			}
		});

		View results = findViewById(R.id.results);
		results.setEnabled(!((Context)getApplication()).getDatabase().getLocationStore().getByDevice(device.getId()).isEmpty());
		results.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Class viewer = ((Context)getApplication()).getSettings().getMapProviderActivity();
				startActivity(new Intent(DeviceDetailsActivity.this, viewer).putExtra("deviceId", device.getId()));
			}
		});

		findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((Context)getApplication()).getDatabase().getDeviceStore().delete(device);
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_device_details, menu);
		return true;
	}

	private void share() {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("text/plain");
		i.putExtra(Intent.EXTRA_SUBJECT, "Sharing Device URI");
		StringBuilder sb = new StringBuilder("bleloc://device/");
		sb.append(HashedMacAddress.fromByteArray(device.getHardwareIdentifier()).toString());
		sb.append('/');
		sb.append(Uri.encode(device.getName()));
		if (!device.getAlias().equals(device.getName())) {
			sb.append('/');
			sb.append(Uri.encode(device.getAlias()));
		}
		i.putExtra(Intent.EXTRA_TEXT, sb.toString());
		startActivity(Intent.createChooser(i, "Share Device URL"));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.item_share:
				share();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
