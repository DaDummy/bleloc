package edu.kit.informatik.pse.bleloc.model;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Persists device entries and provides basic CRUD operations
 */
public class DeviceStore extends GeneralStore{

	private ArrayList<DeviceStoreListener> listeners = new ArrayList<>();

	//the name of the method that returns a user account in a managed Device class
	private final static String GET_USER_METHOD_NAME = "getUserAccount";

	public DeviceStore(EntityManager em) {
		super(em);
		setEntityType(Device.class);
	}

	/**
	 * Adds a device entry to the storage
	 *
	 * @param device
	 * 		the new device entry to be added
	 */
	public void add(Device device){
		//fetch the getUser method to be verified
		Method getUserAccount = getMethod(Device.class, GET_USER_METHOD_NAME);
		//call generic method
		addEntry(device, new Method[]{getUserAccount});

		for (DeviceStoreListener listener : listeners) {
			listener.onAddDevice(device);
		}
	}

	/**
	 * Deletes a device entry from storage
	 *
	 * @param device
	 * 		the device entry to be deleted
	 */
	public void remove(Device device) {
		//fetch the getUser method to be verified
		Method getUserAccount = getMethod(Device.class, GET_USER_METHOD_NAME);
		//call generic method
		removeEntity(device, new Method[]{getUserAccount});

		for (DeviceStoreListener listener : listeners) {
			listener.onRemoveDevice(device);
		}
	}

	/**
	 * Deletes all device entries associated to a specific user account from storage
	 *
	 * @param userAccount
	 * 		the user whose devices are deleted
	 */
	@Transactional(rollbackOn = Exception.class)
	public void removeByUserAccount(UserAccount userAccount) {
		//check argument validity
		if (verifyEntity(userAccount) == EntityState.MANAGED) {
			//fetch list
			List<Device> entryList = getListByColumnValue("user", userAccount.getId());
			//perform batch delete
			int flushCounter = 100;
			for (Device entry : entryList) {
				em.remove(entry);

				for (DeviceStoreListener listener : listeners) {
					listener.onRemoveDevice(entry);
				}

				flushCounter--;
				//perform a periodic sync with database to balance performance and transaction length
				if (flushCounter == 0) {
					em.flush();
					flushCounter = 100;
				}
			}
		}
	}

	/**
	 * Gets a specific device entry
	 *
	 * @param userAccount
	 * 		the associated user account
	 * @param hardwareIdentifier
	 * 		the associated hardware identifier
	 */
	public Device get(UserAccount userAccount, HashedMacAddress hardwareIdentifier) {
		Device result = null;
		//check argument validity
		if(verifyEntity(userAccount) != EntityState.MANAGED) {
			throw new IllegalArgumentException(
				"UserAccount entity cannot enter managed state. May not have been added yet.");
		}
		//prepare query
		Query q = em.createQuery(
			"FROM Device WHERE user = :user AND hwId = :hwId",
			Device.class);
		q.setParameter("user", userAccount);
		q.setParameter("hwId", hardwareIdentifier.toByteArray());
		try {
			result = (Device) q.getSingleResult();
		}
		//ignore no result - return result null
		catch (NoResultException e) {}
		return result;
	}

	/**
	 * Gets all devices entries associated to a specific hardware identifier
	 *
	 * @param hardwareIdentifier
	 * 		the associated hardware identifier
	 */
	public List<Device> getByHardwareIdentifer(HashedMacAddress hardwareIdentifier) {
		return getListByColumnValue("hardwareIdentifier", hardwareIdentifier.toByteArray());
	}

	/**
	 * Gets all devices entries associated to a specific user account
	 *
	 * @param account
	 * 		the associated user account
	 */
	public Collection<Device> getAllByUserAccount(UserAccount account) {
		return getListByColumnValue("user", account.getId());
	}

	/**
	 * Gets an iterable over all hardware identifiers
	 */
	public Iterable<HashedMacAddress> getAllHardwareIdentifiers() {
		ArrayList<HashedMacAddress> result = new ArrayList<>();
		//prepare query
		Query q = em.createQuery("SELECT DISTINCT d.hardwareIdentifier FROM Device d");
		List<byte[]> dbresult = (List<byte[]>) q.getResultList();
		//compile list
		for(byte[] hardwareIdentifier : dbresult) {
			result.add(HashedMacAddress.fromByteArray(hardwareIdentifier));
		}
		return result;
	}

	/**
	 * Gets the count of device entries
	 */
	public long getCount() {
		return getEntryCount();
	}

	/**
	 * Gets the count of device entries associated to a specific user account
	 *
	 * @param account
	 * 		the associated user account
	 */
	public long getCountByUserAccount(UserAccount account) {
		return getEntryCountByColumnValue("user", account.getId());
	}

	/**
	 * Registers the listener.
	 *
	 * @param listener
	 * 		the listener.
	 */
	public void registerDeviceStoreListener(DeviceStoreListener listener) {
		Objects.requireNonNull(listener);
		listeners.add(listener);
	}

	/**
	 * Deregisters the listener.
	 *
	 * @param listener
	 * 		the listener.
	 */
	public void deregisterDeviceStoreListener(DeviceStoreListener listener) {
		listeners.remove(listener);
	}
}
