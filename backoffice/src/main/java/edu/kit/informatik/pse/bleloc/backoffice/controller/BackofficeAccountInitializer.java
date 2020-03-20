package edu.kit.informatik.pse.bleloc.backoffice.controller;

import edu.kit.informatik.pse.bleloc.model.BackofficeAccount;
import edu.kit.informatik.pse.bleloc.model.BackofficeAccountStore;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.transaction.*;

@WebListener
public class BackofficeAccountInitializer implements ServletContextListener {

	private static final String DefaultAdminAccountUserName = "admin";

	@PersistenceContext
	EntityManager em;

	@Resource
	UserTransaction tx;

	public void contextInitialized(ServletContextEvent event) {
		try
		{
			tx.begin();

			BackofficeAccountStore accountStore = new BackofficeAccountStore(em);

			if (accountStore.get(DefaultAdminAccountUserName) == null) {
				// TODO(ca): Generate random admin password
				String password = DefaultAdminAccountUserName;

				BackofficeAccount backofficeAccount = new BackofficeAccount(DefaultAdminAccountUserName, "admin");

				accountStore.add(backofficeAccount);

				System.out.println(
					"Created default admin account '" + backofficeAccount.getName() + "' with password '" + password + '"');
			}

			tx.commit();
		} catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
			throw new RuntimeException(e);
		}
	}
}
