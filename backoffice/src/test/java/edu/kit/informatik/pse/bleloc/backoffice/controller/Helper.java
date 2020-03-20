package edu.kit.informatik.pse.bleloc.backoffice.controller;

import javax.persistence.EntityManager;
import javax.transaction.*;

public class Helper {
	public static void initializeBackofficeSettingsInitializer(BackofficeSettingsInitializer initializer, EntityManager em) {
		initializer.em = em;
		initializer.tx = new UserTransaction() {
			@Override
			public void begin() throws NotSupportedException, SystemException {
				em.getTransaction().begin();
			}

			@Override
			public void commit()
				throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException,
				       IllegalStateException, SystemException {
				em.getTransaction().commit();
			}

			@Override
			public void rollback() throws IllegalStateException, SecurityException, SystemException {
				em.getTransaction().rollback();
			}

			@Override
			public void setRollbackOnly() throws IllegalStateException, SystemException {
				em.getTransaction().setRollbackOnly();
			}

			@Override
			public int getStatus() throws SystemException {
				return 0;
			}

			@Override
			public void setTransactionTimeout(int i) throws SystemException {

			}
		};
	}
}
