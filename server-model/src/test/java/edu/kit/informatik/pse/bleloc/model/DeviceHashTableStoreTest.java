package edu.kit.informatik.pse.bleloc.model;

import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.ArrayList;

import static org.junit.Assert.fail;

public class DeviceHashTableStoreTest {
	@Test
	public void replaceAndGet() {
		DeviceHashTableStore store = new DeviceHashTableStore();
		DeviceHashTable createdTable = new DeviceHashTable(42);
		store.replace(createdTable);
		DeviceHashTable returnedTable = store.get();
		Assert.assertEquals(createdTable.getLastUpdateTime().getTime(), returnedTable.getLastUpdateTime().getTime());
	}
}
