package edu.kit.informatik.pse.bleloc.backoffice.controller;

import edu.kit.informatik.pse.bleloc.backoffice.BackofficeTest;
import edu.kit.informatik.pse.bleloc.model.BackofficeAccountStore;
import org.junit.Assert;
import org.junit.Test;

import javax.transaction.*;

public class BackofficeAccountInitializerTest extends BackofficeTest {

	@Test
	public void testBackofficeAccountInitializer() {
		BackofficeAccountInitializer backofficeAccountInitializer = new BackofficeAccountInitializer();
		backofficeAccountInitializer.em = em;
		backofficeAccountInitializer.tx = new UserTransaction() {
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

		backofficeAccountInitializer.contextInitialized(null);
	}

}
