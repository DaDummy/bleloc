package edu.kit.informatik.pse.bleloc.backend.controller;

import edu.kit.informatik.pse.bleloc.model.DeviceHashTableManager;
import edu.kit.informatik.pse.bleloc.model.DeviceHashTableStore;
import edu.kit.informatik.pse.bleloc.model.DeviceStore;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.transaction.*;

@WebListener
public class DeviceHashTableInitializer implements ServletContextListener {
	@PersistenceContext
	EntityManager em;

	@Resource
	UserTransaction tx;

	@Inject
	DeviceHashTableStore deviceHashTableStore;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		try
		{
			tx.begin();

			DeviceStore deviceStore = new DeviceStore(em);

			DeviceHashTableManager deviceHashTableManager = new DeviceHashTableManager(deviceStore, deviceHashTableStore);
			deviceHashTableManager.recreateDeviceHashTable();

			tx.commit();
		} catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// Nothing to do in here
	}
}
