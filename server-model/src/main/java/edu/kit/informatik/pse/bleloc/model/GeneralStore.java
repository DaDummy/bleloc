package edu.kit.informatik.pse.bleloc.model;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class GeneralStore {
	protected EntityManager em;
	protected CriteriaBuilder cb;
	protected Class entryType = AbstractEntry.class;
	protected enum EntityState {MANAGED, TRANSIENT, DETACHED, DELETED};

	protected GeneralStore(EntityManager em) {
		this.em = em;
		this.cb = em.getCriteriaBuilder();
	}

	/**
	 * Sets the Entity Type managed in this store.
	 * This is referenced by many operations. please set inside the constructor
	 * @param entityCls a class that inherits from AbstractEntry
	 */
	protected void setEntityType(Class entityCls) {
		if (!AbstractEntry.class.isAssignableFrom(entityCls)) {
			throw new IllegalArgumentException("Entity is of the wrong class for this store.");
		}
		this.entryType = entityCls;
	}

	/**
	 * Generic Store method for adding corresponding Entry objects
	 * Performs all necessary entity state checks and includes verification of associated relationships (one level deep)
	 * @param entry an entry to be added to the store. Must be of the associated store/entry type
	 * @param verifyEntities Reflection Methods which can be invoked upon the entry to obtain a valid AbstractEntry subclass.
	 * These associated Entities will be verified before attempting the operation.
	 * @param <T> an implemented subclass of AbstractEntry.
	 */
	@Transactional(rollbackOn = Exception.class)
	protected <T extends AbstractEntry> void addEntry(T entry, Method[] verifyEntities) {
		//check entry type
		if(!entryType.isInstance(entry)) {
			throw new IllegalArgumentException("Entity is of the wrong class for this store.");
		}
		try {
			//check argument validity
			EntityState argState;
			for (Method m : verifyEntities) {
				argState = verifyEntity(m.invoke(entry));
				if (argState != EntityState.MANAGED) {
					throw new EntityNotFoundException(String.format(
						"Failed to verify dependent Entity for %s via %s",entry.getClass(), m.getName()));
				}
			}
			//save entity
			em.persist(entry);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new IllegalArgumentException(String.format(
				"Failed to verify dependent Entity for %s",entry.getClass()));
		}
	}

	/**
	 * Generic Store method for updating corresponding Entry objects
	 * Performs all necessary entity state checks and includes verification of associated relationships (one level deep)
	 * @param entry the entry to be updated. Must be of the associated store/entry type
	 * @param verifyEntities Reflection Methods which can be invoked upon the entry to obtain a valid AbstractEntry subclass.
	 * These associated Entities will be verified before attempting the operation.
	 * @param <T> an implemented subclass of AbstractEntry.
	 */
	@Transactional(rollbackOn = Exception.class)
	protected <T extends AbstractEntry> void updateEntity(T entry, Method[] verifyEntities) {
		//check entry type
		if(!entryType.isInstance(entry)) {
			throw new IllegalArgumentException();
		}
		try {
			//check relationship argument validity
			EntityState argState;
			for (Method m : verifyEntities) {
				argState = verifyEntity(m.invoke(entry));
				if (argState == EntityState.TRANSIENT || argState == EntityState.DELETED) {
					throw new EntityNotFoundException();
				}
				else if (argState != EntityState.MANAGED) {
					throw new IllegalArgumentException();
				}
			}
			//check entity validity
			argState = verifyEntity(entry);
			if (argState == EntityState.TRANSIENT || argState == EntityState.DELETED) {
				throw new EntityNotFoundException();
			}
			else if (argState != EntityState.MANAGED) {
				throw new IllegalArgumentException();
			}
			//nothing is actually to be done, because verifyEntry() performs a merge already
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new IllegalArgumentException(String.format(
				"Failed to verify dependent Entity for %s",entry.getClass()));
		}
	}

	/**
	 * Generic Store method for deleting corresponding Entry objects from the Store
	 * Performs all necessary entity state checks and includes verification of associated relationships (one level deep)
	 * @param entry the entry to be deleted. Must be of the associated store/entry type
	 * @param verifyEntities Reflection Methods which can be invoked upon the entry to obtain a valid AbstractEntry subclass.
	 * These associated Entities will be verified before attempting the operation.
	 * @param <T> an implemented subclass of AbstractEntry.
	 */
	@Transactional(rollbackOn = Exception.class)
	protected <T extends AbstractEntry> void removeEntity(T entry, Method[] verifyEntities) {
		//check entry type
		if (!entryType.isInstance(entry)) {
			throw new IllegalArgumentException();
		}
		try {
			//check relationship argument validity
			boolean validFlag = true;
			for (Method m : verifyEntities) {
				 if (verifyEntity(m.invoke(entry)) != EntityState.MANAGED) {
					 validFlag = false;
				}
			}
			//check entity validity
			if (validFlag && verifyEntity(entry) == EntityState.MANAGED) {
				// perform operation
				em.remove(entry);
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new IllegalArgumentException(String.format(
				"Failed to verify dependent Entity for %s",entry.getClass()));
		}
	}

	/**
	 * Generic store method for retrieving an entry item by his id
	 * @param id id of the entry
	 * @param <T> an implemented subclass of AbstractEntry.
	 * @return entry item corresponding to the id. null if none exists.
	 */
	protected <T extends AbstractEntry> T getEntry(Long id) {
		T result = null;
		if (id != null) {
			//check if entity in DB
			result = (T) em.find(entryType, id);
		}
		return result;
	}

	/**
	 * Generic Store method for retrieving a List of Entry objects from the Store
	 * @param <T> an implemented subclass of AbstractEntry.
	 * @return an ArrayList of Entity objects
	 */
	protected <T extends AbstractEntry> List<T> getList() {
		List<T> result;
		Query q = em.createQuery(queryList());
		result = (List<T>) q.getResultList();
		return result;
	}

	/**
	 * Generic Store method for retrieving a List of Entry objects from the Store
	 * @param <T> an implemented subclass of AbstractEntry.
	 * @return an ArrayList of Entity objects
	 */
	protected <T extends AbstractEntry> List<T> getListPagination(int pageSize, int pageNumber) {
		List<T> result;
		Query q = em.createQuery(queryList());
		q.setFirstResult((pageNumber-1) * pageSize);
		q.setMaxResults(pageSize);
		result = (List<T>) q.getResultList();
		return result;
	}

	/**
	 * Generic Store method for checking if an Entry with certain column value exists
	 * @param colName name of the column
	 * @param colValue value to be selected for
	 * @param <T> an implemented subclass of AbstractEntry.
	 * @param <E> a value type that can be converted to String
	 * @return true, if value is in the database, false if not
	 */
	protected <T extends AbstractEntry, E> boolean existsEntry(String colName, E colValue) {
		Long count;
		count = getEntryCountByColumnValue(colName, colValue);
		if (count > 0){
			return true;
		}
		return false;
	}

	/**
	 * Generic Store method for retrieving a Count of Entry objects from the Store that have a certain column value
	 * NO TRANSACTION is established inside this method. You are supposed to call it from within
	 * transactional boundaries.
	 * @param colName name of the column
	 * @param colValue value to be selected for
	 * @param <T> an implemented subclass of AbstractEntry.
	 * @param <E> a value type that can be converted to String
	 * @return a Long with the count of objects that correspond to the selection criteria
	 */
	protected <T extends AbstractEntry, E> Long getEntryCountByColumnValue(String colName, E colValue) {
		Long result;
		CriteriaQuery<Long> countQueryCriteria = queryCountByColumnValue(colName, colValue);
		Query q = em.createQuery(countQueryCriteria);
		result = (Long) q.getSingleResult();
		return result;
	}

	/**
	 * Generic Store method for retrieving a Count of Entry objects from the Store
	 * NO TRANSACTION is established inside this method. You are supposed to call it from within
	 * transactional boundaries.
	 * @param <T> an implemented subclass of AbstractEntry.
	 * @return a Long with the count of objects that correspond to the selection criteria
	 */
	protected <T extends AbstractEntry> Long  getEntryCount() {
		Long result;
		CriteriaQuery<Long> countQueryCriteria = queryCount();
		Query q = em.createQuery(countQueryCriteria);
		result = (Long) q.getSingleResult();
		return result;
	}

	/**
	 * Generic Store method for retrieving a List of Entry objects from the Store that have a certain column value
	 * NO TRANSACTION is established inside this method. You are supposed to call it from within
	 * transactional boundaries.
	 * @param colName name of the column
	 * @param colValue value to be selected for
	 * @param <T> an implemented subclass of AbstractEntry.
	 * @param <E> a value type that can be converted to String
	 * @return an ArrayList of Entity objects that correspond to the selection criteria
	 */
	protected <T extends AbstractEntry, E> List<T> getListByColumnValue(String colName, E colValue) {
		List<T> result;
		Query q = em.createQuery(querySelectByColumnValue(colName, colValue));
		result = (List<T>) q.getResultList();
		return result;
	}

	/**
	 * Creates query criteria for certain column values
	 * SELECT * FROM entity WHERE colName = colValue
	 * @param colName name of the column
	 * @param colValue value to be selected for
	 * @param <T> an implemented subclass of AbstractEntry.
	 * @param <E> a value type that can be converted to String
	 * @return query criteria
	 */
	protected <T, E> CriteriaQuery querySelectByColumnValue(String colName, E colValue) {
		CriteriaQuery<T> criteria = cb.createQuery(entryType);
		Root<T> entry = criteria.from(entryType);
		criteria.where(cb.equal(entry.get(colName), colValue));
		return criteria;
	}

	/**
	 * Creates query criteria for the count of certain column values
	 * SELECT COUNT(*) FROM entity WHERE colName = colValue
	 * @param colName name of the column
	 * @param colValue value to be selected for
	 * @param <T> an implemented subclass of AbstractEntry.
	 * @param <E> a value type that can be converted to String
	 * @return query criteria
	 */
	protected <T, E> CriteriaQuery queryCountByColumnValue(String colName, E colValue) {
		CriteriaQuery<Long> criteria = cb.createQuery(entryType);
		Root<T> entry = criteria.from(entryType);
		criteria.select(cb.count(entry));
		criteria.where(cb.equal(entry.get(colName), colValue));
		return criteria;
	}
	/**
	 * Creates query criteria for the count of all entries
	 * SELECT COUNT(*) FROM entity
	 * @param <T> an implemented subclass of AbstractEntry.
	 * @return query criteria
	 */
	protected <T> CriteriaQuery queryCount() {
		CriteriaQuery<Long> criteria = cb.createQuery(entryType);
		Root<T> entry = criteria.from(entryType);
		criteria.select(cb.count(entry));
		return criteria;
	}

	/**
	 * Creates query criteria for selecting all entries
	 * SELECT * FROM entity
	 * @param <T> an implemented subclass of AbstractEntry.
	 * @return query criteria
	 */
	protected <T> CriteriaQuery queryList() {
		CriteriaQuery<Long> criteria = cb.createQuery(entryType);
		Root<T> entry = criteria.from(entryType);
		return criteria;
	}


	/**
	 * Verifies the state of the entity. The goal is to either confirm the entity as invalid or
	 * adjust the entity state to MANAGED in which case it is regarded as valid. If the entity is
	 * detached a merge operation will be attempted.
	 * PRECONDITION: the entity has a method getId:Long which returns the entity id
	 * NO TRANSACTION is established inside this method. You are supposed to call it from within
	 * transactional boundaries.
	 * @param entity the Entity object to be verified
	 * @return false if the entity is transient or deleted, true if it is managed
	 */
	protected EntityState verifyEntity(Object entity) {
		EntityState result = null;
		try {
			Class cls = entity.getClass();
			//check if object is an entity
			if(cls.getAnnotation(Entity.class) == null) {
				throw new IllegalArgumentException("entity parameter should be annotated as an @Entity");
			}
			// get the id
			Method getId = cls.getDeclaredMethod("getId");
			Long id = (Long) getId.invoke(entity);
			// entity is transient
			if(id == null) {
				result = EntityState.TRANSIENT;
			}
			// entity is managed
			else if(em.contains(entity)){
				result = EntityState.MANAGED;
			}
			// entity is deleted
			else if(em.find(entity.getClass(), id) == null){
				result = EntityState.DELETED;
			}
			// entity is detached
			else{
				try {
					em.merge(entity);
				} catch (Exception e) {
					result = EntityState.DETACHED;
				}
				result = EntityState.MANAGED;
			}
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Entity must have a getId() method." + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(
				"Entity must have a getId() method that cannot be private." + e.getMessage());
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("getId() method has caused an exception." + e.getMessage());
		}
		return result;
	}

	protected <T> Method getMethod(Class<T> cls, String methodName) {
		Method method = null;
		try {
			method = cls.getDeclaredMethod(methodName);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(
				String.format("Tried getting method %s from class %s. Method does not seem to exist",
				              cls.getSimpleName(), methodName) + e.getMessage());
		}
		return method;
	}
}
