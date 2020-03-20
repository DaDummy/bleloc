package edu.kit.informatik.pse.bleloc.model;

import javax.persistence.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Persists user accounts and provides basic CRUD operations
 */
public class UserAccountStore extends GeneralStore {

	public UserAccountStore(EntityManager em) {
		super(em);
		setEntityType(UserAccount.class);
	}

	/**
	 * Adds an user entry to the storage
	 *
	 * @param userAccount
	 * 		the new user account to be added
	 */
	public void add(UserAccount userAccount){
		if (existsEntry("name", userAccount.getName())) {
			// TODO(ca) Why is this detach here?
			em.detach(userAccount);
			throw new EntityExistsException("Duplicate user name");
		}
		addEntry(userAccount, new Method[]{});
	}

	/**
	 * Updates an user account in the storage
	 *
	 * @param userAccount
	 * 		the modified user account entry
	 */
	public void update(UserAccount userAccount) {
		//call generic method
		updateEntity(userAccount, new Method[]{});
	}

	/**
	 * Deletes an user account from storage
	 *
	 * @param userAccount
	 * 		the user account to be deleted
	 */
	public void remove(UserAccount userAccount) {
		removeEntity(userAccount, new Method[]{});
	}

	/**
	 * Gets entry of the user account
	 *
	 * @param name
	 * 		the login name of the user entry
	 */

	public UserAccount get(String name) {
		List<AbstractEntry> result;
		result = getListByColumnValue("name", name);
		if(result.size() == 0){
			return null;
		}
		return (UserAccount) result.get(0);
	}

	/**
	 * Gets entry of the user account
	 *
	 * @param id
	 * 		the id of the user entry
	 */

	public UserAccount get(Long id) {
		return getEntry(id);
	}

	/**
	 * Gets a list of user entries from storage. The list is ordered by userId
	 *
	 * @param pageSize
	 * 		number of items to put into the list
	 * @param pageNumber
	 * 		page number. starts with 1
	 * @return a List of UserAccount entries
	 */

	public List<UserAccount> list(int pageSize, int pageNumber) {
		if(pageSize <= 0 || pageNumber < 1) {
			throw new IllegalArgumentException("Illegal page boundaries");
		}
		return getListPagination(pageSize, pageNumber);
	}

	/**
	 * Gets the number of user entries in the storage
	 *
	 * @return number of entries
	 */

	public Long getCount() {
		return getEntryCount();
	}

}
