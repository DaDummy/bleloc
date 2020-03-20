package edu.kit.informatik.pse.bleloc.client.controller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import edu.kit.informatik.pse.bleloc.client.R;
import edu.kit.informatik.pse.bleloc.client.controller.Context;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.UserDataSyncEventListener;
import edu.kit.informatik.pse.bleloc.client.model.device.Device;
import edu.kit.informatik.pse.bleloc.client.model.user.AuthenticationResult;
import edu.kit.informatik.pse.bleloc.client.model.user.LogoutEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DashboardActivity extends SettingsMenuActivityBase implements LogoutEventListener {
	static int MinimumSecondsBetweenSyncs = 60;

	Date lastSync = null;

	private void reloadDevices() {
		List<Device> devices = ((Context) getApplication()).getDatabase().getDeviceStore().getDevices();

		RecyclerView rv = findViewById(R.id.listView);
		runOnUiThread(() -> ((RecyclerViewAdapter)rv.getAdapter()).updateDeviceList(devices));

		Log.d("DashboardActivity", "Reloaded devices");
	}

	/**
	 * Activity to view the list of registered BT-Devices.
	 * <p>
	 * Creating Activity.
	 *
	 * @param savedInstanceState
	 * 		to create Activity data.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);

		RecyclerView rv = findViewById(R.id.listView);

		rv.setLayoutManager(new LinearLayoutManager(this));

		rv.setAdapter(new RecyclerViewAdapter(this, new ArrayList<>()));

		reloadDevices();

		findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(DashboardActivity.this, AddDeviceActivity.class));
			}
		});

		((Context)getApplication()).getAuthenticationManager().registerListener(this);

		((Context)getApplication()).getUserDataSyncManager().registerEventListener(new UserDataSyncEventListener() {
			@Override
			public void onPreSync() {
				// Nothing to do in here
			}

			@Override
			public void onPostSync() {
				reloadDevices();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		((Context)getApplication()).getAuthenticationManager().deregisterListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		reloadDevices();

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, -MinimumSecondsBetweenSyncs);
		if (lastSync == null || lastSync.before(calendar.getTime())) {
			((Context) getApplication()).getUserDataSyncManager().triggerSync();
		}
	}

	public void onReceiveLogoutResult(AuthenticationResult result) {
		startActivity(new Intent(this, LoginActivity.class));
		finish();
	}

	static class RecyclerViewAdapter extends RecyclerView.Adapter {
		private DashboardActivity activity;
		private List<Device> devices;

		public RecyclerViewAdapter(DashboardActivity activity, List<Device> devices) {
			this.activity = activity;
			this.devices = devices;
		}

		public void updateDeviceList(List<Device> devices) {
			this.devices = devices;
			notifyDataSetChanged();
		}

		class ViewHolder extends RecyclerView.ViewHolder {
			private TextView label;
			private Button toggle;
			private View results;
			private Device d;
			private boolean tracked;

			ViewHolder(View view) {
				super(view);
				label = view.findViewById(R.id.label);
				view.findViewById(R.id.details).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						activity.startActivity(new Intent(activity, DeviceDetailsActivity.class).putExtra("deviceId", d.getId()));
					}
				});
				toggle = view.findViewById(R.id.toggle);
				toggle.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						((Context)activity.getApplication()).getTrackingStateManager().toggleDeviceTrackingStatus(d, tracked = !tracked);
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								bind(d);
							}
						}, 1000);
					}
				});
				results = view.findViewById(R.id.results);
				results.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Class viewer = ((Context)activity.getApplication()).getSettings().getMapProviderActivity();
						activity.startActivity(new Intent(activity, viewer).putExtra("deviceId", d.getId()));
					}
				});
			}

			void bind(Device d) {
				this.d = d;
				tracked = ((Context)activity.getApplication()).getTrackingStateManager().isTracked(d);
				label.setText(d.getAlias());
				toggle.setText(tracked ? "Disable tracking" : "Enable tracking");
				results.setEnabled(!((Context)activity.getApplication()).getDatabase().getLocationStore().getByDevice(d.getId()).isEmpty());
			}
		}

		@NonNull
		@Override
		public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
			return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false));
		}

		@Override
		public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
			((ViewHolder)viewHolder).bind(devices.get(i));
		}

		@Override
		public int getItemCount() {
			return devices.size();
		}
	}
}
