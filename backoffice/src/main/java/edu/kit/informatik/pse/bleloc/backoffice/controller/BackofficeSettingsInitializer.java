package edu.kit.informatik.pse.bleloc.backoffice.controller;

import edu.kit.informatik.pse.bleloc.model.settings.Setting;
import edu.kit.informatik.pse.bleloc.model.settings.SettingStore;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.transaction.*;
import java.util.Arrays;
import java.util.List;

@WebListener
public class BackofficeSettingsInitializer implements ServletContextListener {

	// contains all settings - may be replaced with a config file
	private static final List<Object[]> SETTINGS = Arrays.asList(
		new Object[]{
			"maxDeviceNumber",
			"general",
			"Maximal Number of Tracked Devices",
			"Names the number of devices a user is allowed to set tracked.",
			Setting.Type.NUMBER,
			"5",
		},
		new Object[]{
			"trackingDuration",
			"general",
			"Tracking Duration (days)",
			"After this many days, a device is reset from being tracked to not tracked.",
			Setting.Type.NUMBER,
			"14",
		},
		new Object[]{
			"statistics",
			"plugins",
			"Enable Settings",
			"Enables data collection routines, that gather the information to be displayed on the statistics site.",
			Setting.Type.BOOLEAN,
			"false",
		},
		new Object[]{
			"gamification",
			"plugins",
			"Enable Gamification",
			"Enables the gamification aspect, that e.g. are achievements and scoreboards.",
			Setting.Type.BOOLEAN,
			"false",
		},
		new Object[]{
			"bounty",
			"plugins",
			"Enable Bounty",
			"Enables the bounty system in that every user is granted a reward for finding devices.",
			Setting.Type.BOOLEAN,
			"false",
		},
		new Object[]{
			"publicBeacons",
			"plugins",
			"Enable Public Beacons",
			"Enables the public API for accessing data of public beacons.",
			Setting.Type.BOOLEAN,
			"false",
		}
	);

	@PersistenceContext
	EntityManager em;

	@Resource
	UserTransaction tx;

	public void contextInitialized(ServletContextEvent event) {
		try
		{
			tx.begin();

			SettingStore settingStore = new SettingStore(em);
			Setting setting;
			for (Object[] settingData : SETTINGS) {
				setting = settingStore.get((String) settingData[1], (String) settingData[0]);
				if (setting == null) {
					setting = Setting.createSetting((String) settingData[0],
					                                (String) settingData[1],
					                                (String) settingData[2],
					                                (String) settingData[3],
					                                (Setting.Type) settingData[4],
					                                (String) settingData[5]);
					settingStore.add(setting);
				}
			}

			tx.commit();
		} catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
			throw new RuntimeException(e);
		}
	}
}
