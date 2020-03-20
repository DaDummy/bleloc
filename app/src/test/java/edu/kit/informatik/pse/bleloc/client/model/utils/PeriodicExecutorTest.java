package edu.kit.informatik.pse.bleloc.client.model.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLooper;

@RunWith(RobolectricTestRunner.class)
public class PeriodicExecutorTest {

	private static PeriodicExecutor periodicExecutor;

	@Mock
	private static Runnable callback;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		periodicExecutor = new PeriodicExecutor(100, callback);
	}

	@Test
	public void initialTest(){
		Assert.assertNotNull(periodicExecutor);
		ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
	}

	@Test
	public void setTimerActiveTest(){
		periodicExecutor.setTimerActive(true);
		periodicExecutor.setTimerActive(false);
	}
}


