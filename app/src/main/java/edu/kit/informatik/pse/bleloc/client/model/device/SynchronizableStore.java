package edu.kit.informatik.pse.bleloc.client.model.device;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

import java.util.List;

/**
 * A store for ojects that can be synchronized via server.
 */
public abstract class SynchronizableStore<E extends SynchronizableObject> {

	/**
	 * Creates a {@link java.util.Set} view of the store's content.
	 *
	 * @return A set view of the store
	 */
	public abstract List<E> getSyncIndex();

	/**
	 * Adds a new element to the store.<br> The operation fails if this element is already contained.
	 *
	 * @param syncronizableObject
	 * 		The element to add
	 */
	@Insert
	public abstract long add(E syncronizableObject);

	/**
	 * Updates an element in the store.<br> An element that is considered equal to the input, is replaced by the new
	 * one.
	 *
	 * @param syncronizableObject
	 * 		The element to update to
	 */
	public void update(E syncronizableObject) {
		syncronizableObject.dataChanged();
		syncUpdate(syncronizableObject);
	}

	@Update
	public abstract void syncUpdate(E syncronizableObject);

	/**
	 * Removes the given element from the store.
	 *
	 * @param syncronizableObject
	 * 		The element to remove
	 */
	public void delete(E syncronizableObject) {
		syncronizableObject.setDeleted(true);
		syncronizableObject.dataChanged();
		update(syncronizableObject);
	}

	@Delete
	public abstract void syncDelete(E location);
}
