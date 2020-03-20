package edu.kit.informatik.pse.bleloc.model;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TrackingResultStore extends GeneralStore {

	public TrackingResultStore(EntityManager em){
		super(em);
		setEntityType(TrackingResult.class);
	}

	/**
	 * Adds a tracking result to the storage
	 *
	 * @param trackingResult
	 * 		the new tracking result to be added
	 */
	public void add(TrackingResult trackingResult) {
		//fetch the getDevice method to be verified
		Method getDevice = getMethod(TrackingResult.class, "getDevice");
		//call generic method
		addEntry(trackingResult, new Method[]{getDevice});
	}

	/**
	 * Deletes a tracking result from storage
	 *
	 * @param trackingResult
	 * 		the tracking result to be deleted
	 */
	public void remove(TrackingResult trackingResult) {
		//fetch the getDevice method to be verified
		//Method getDevice = getMethod(TrackingResult.class, "getDevice");
		//call generic method
		removeEntity(trackingResult, new Method[]{/*getDevice*/});
	}

	/**
	 * Deletes the tracking results linked to a specific device
	 *
	 * @param device
	 * 		the device whose tracking results should be deleted
	 */
	@Transactional(rollbackOn = Exception.class)
	public void removeByDevice(Device device) {
		//check argument validity
		if(verifyEntity(device) == EntityState.MANAGED) {
			//fetch list
			List<TrackingResult> entryList = getListByColumnValue("device", device.getId());
			//perform batch delete
			int flushCounter = 100;
			for (TrackingResult entry : entryList) {
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
	 * Gets the tracking result with a specific id
	 *
	 * @param trackingResultId
	 * 		the id
	 * @return the tracking result
	 */
	public TrackingResult get(long trackingResultId) {
		return getEntry(trackingResultId);
	}

	/**
	 * Gets the n-th oldest tracking result associated to a specific user account
	 *
	 * @param account
	 * 		the user account
	 * @param n
	 * 		n
	 * @return the n-th oldest tracking result
	 */
	public TrackingResult getNthOldestByUserAccount(UserAccount account, int n) {
		Query q = em.createQuery(
			"SELECT t FROM TrackingResult AS t INNER JOIN Device AS d ON t.device = d.id WHERE d.user = :user ORDER BY t.encounteredAt ASC",
			TrackingResult.class);
		q.setParameter("user", account);
		ArrayList<TrackingResult> resultList;
		resultList = (ArrayList<TrackingResult>) q.getResultList();

		if(resultList.size() <= n) {
			return null;
		}
		else{
			return resultList.get(n);
		}
	}
}
