package edu.kit.informatik.pse.bleloc.model;

import edu.kit.informatik.pse.bleloc.payload.UserDataIndexEntry;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Persists user data entries and provides basic CRUD operations
 */
public class UserDataStore extends GeneralStore {

	private final static String GET_USER_METHOD_NAME = "getUser";

	public UserDataStore(EntityManager em) {
		super(em);
		setEntityType(UserDataEntry.class);
	}

	/**
	 * Adds an user data entry to the storage
	 *
	 * @param entry
	 * 		the new user data entry to be added
	 */
	public void add(UserDataEntry entry) {
		//fetch the getUser method to be verified
		Method getUser = getMethod(UserDataEntry.class, GET_USER_METHOD_NAME);
		//call generic method
		addEntry(entry, new Method[]{getUser});
	}

	/**
	 * Updates a user data entry in the storage
	 *
	 * @param entry
	 * 		the modified user daata entry
	 */

	public void update(UserDataEntry entry) {
		//fetch the getUser method to be verified
		Method getUser = getMethod(UserDataEntry.class, GET_USER_METHOD_NAME);
		//call generic method
		updateEntity(entry, new Method[]{getUser});
	}

	/**
	 * Deletes an user data entry from storage
	 *
	 * @param entry
	 * 		the user data entry to be deleted
	 */

	public void remove(UserDataEntry entry) {
		//fetch the getUser method to be verified
		Method getUser = getMethod(UserDataEntry.class, GET_USER_METHOD_NAME);
		//call generic method
		removeEntity(entry, new Method[]{getUser});
	}

	/**
	 * Gets the user data entry with a specific id associated to a specific user
	 *
	 * @param userAccount
	 * 		the account
	 * @param id
	 * 		the id
	 * @return the user data entry
	 */

	public UserDataEntry get(UserAccount userAccount, Long id) {
		UserDataEntry result = null;
		//check argument validity
		if(verifyEntity(userAccount) != EntityState.MANAGED) {
			throw new EntityNotFoundException("User does not seem to exist");
		}
		//prepare query
		Query q = em.createQuery(
			"FROM UserDataEntry WHERE userId = :userIdParam AND id = :entryIdParam",
			UserDataEntry.class);
		q.setParameter("userIdParam", userAccount.getId());
		q.setParameter("entryIdParam", id);
		try {
			result = (UserDataEntry) q.getSingleResult();
		}
		//ignore no result - return result null
		catch (NoResultException e) {}
		return result;
	}

	/**
	 * Deletes the user data entries associated to a specific user account Because reverting a batch delete would be
	 * annoying, entities that are already missing from DB are ignored and do not throw Exceptions.
	 *
	 * @param userAccount
	 * 		the user account whose entries should be deleted
	 */

	@Transactional(rollbackOn = Exception.class)
	public void removeByUserAccount(UserAccount userAccount) {
		//check argument validity
		if (verifyEntity(userAccount) == EntityState.MANAGED) {
			//fetch list
			List<UserDataEntry> entryList = getListByColumnValue("user", userAccount.getId());
			//perform batch delete
			int flushCounter = 100;
			for (UserDataEntry entry : entryList) {
				em.remove(entry);
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
	 * Gets the index of user data entries associated to a specific user
	 *
	 * @param userAccount
	 * 		the account
	 * @return the collection of UserDataIndexEntry
	 */
	public Collection<UserDataIndexEntry> getIndexByUserAccount(UserAccount userAccount) {
		Collection<UserDataEntry> entryList;
		Collection<UserDataIndexEntry> resultList = new ArrayList<UserDataIndexEntry>();
		//check argument validity
		if(verifyEntity(userAccount) != EntityState.MANAGED) {
			throw new EntityNotFoundException("User does not seem to exist");
		}
		//fetch list
		entryList = getListByColumnValue("user", userAccount.getId());
		//create Index from DataEntries
		for (UserDataEntry entry : entryList) {
			resultList.add(new UserDataIndexEntry(entry.getId(), entry.getModifiedAt()));
		}
		return resultList;
	}
}