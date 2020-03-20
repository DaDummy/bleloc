package edu.kit.informatik.pse.bleloc.model;

import javax.persistence.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Persists Backoffice accounts and provides basic CRUD operations
 */
public class BackofficeAccountStore extends GeneralStore {

	public BackofficeAccountStore(EntityManager em) {
		super(em);
		setEntityType(BackofficeAccount.class);
	}

	/**
	 * Adds an backoffice entry to the storage
	 *
	 * @param backofficeAccount
	 * 		the new backoffice account to be added
	 */
	public void add(BackofficeAccount backofficeAccount) throws RollbackException{
		if (existsEntry("name", backofficeAccount.getName())) {
			// TODO(ca) Why is this detach here?
			em.detach(backofficeAccount);
			throw new EntityExistsException("Duplicate user name");
		}
		addEntry(backofficeAccount, new Method[]{});
	}

	/**
	 * Updates an backoffice account in the storage
	 *
	 * @param backofficeAccount
	 * 		the modified backoffice account entry
	 */
	public void update(BackofficeAccount backofficeAccount) {
		//call generic method
		updateEntity(backofficeAccount, new Method[]{});
	}

	/**
	 * Deletes an backoffice account from storage
	 *
	 * @param backofficeAccount
	 * 		the backoffice account to be deleted
	 */
	public void remove(BackofficeAccount backofficeAccount) {
		removeEntity(backofficeAccount, new Method[]{});
	}

	/**
	 * Gets entry of the backoffice account
	 *
	 * @param name
	 * 		the login name of the backoffice entry
	 */

	public BackofficeAccount get(String name) {
		List<AbstractEntry> result;
		result = getListByColumnValue("name", name);
		if(result.size() == 0){
			return null;
		}
		return (BackofficeAccount) result.get(0);
	}

	/**
	 * Gets entry of the backoffice account
	 *
	 * @param id
	 * 		the id of the backoffice entry
	 */
	public BackofficeAccount get(long id) {
		return getEntry(id);
	}

	/**
	 * Gets a list of backoffice entries from storage. The list is ordered by backofficeId
	 *
	 * @param pageSize
	 * number of items to put into the list
	 * @param pageNumber
	 * 	page number. starts with 1
	 * @return a List of BackofficeAccount entries
	 */

	public List<BackofficeAccount> list(int pageSize, int pageNumber) {
		if(pageSize <= 0 || pageNumber < 1) {
			throw new IllegalArgumentException("Illegal page boundaries");
		}
		return getListPagination(pageSize, pageNumber);
	}

	/**
	 * Gets the number of backoffice entries in the storage
	 *
	 * @return number of entries
	 */
	public long getCount() {
		return getEntryCount();
	}
}
