package edu.kit.informatik.pse.bleloc.client.controller.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import edu.kit.informatik.pse.bleloc.client.R;

public class SettingsMenuActivityBase extends AppCompatActivity {

	/**
	 * Menu creation.
	 *
	 * @param menu
	 * 		is user interface element that allows to select one of several listed program options.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_dashboard, menu);
		return true;
	}

	/**
	 * Menu item handler.
	 *
	 * @param item
	 * 		is menu item, that was clicked
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.item_settings:
				startActivity(new Intent(SettingsMenuActivityBase.this, SettingsActivity.class));
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
