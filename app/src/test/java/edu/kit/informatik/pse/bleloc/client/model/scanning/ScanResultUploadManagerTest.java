package edu.kit.informatik.pse.bleloc.client.model.scanning;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.test.runner.AndroidJUnit4;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.requests.RequestManager;
import edu.kit.informatik.pse.bleloc.client.model.user.UserData;
import edu.kit.informatik.pse.bleloc.model.HashedMacAddress;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class ScanResultUploadManagerTest {
	private static ScanResultUploadManager scanResultUploadManager;
	private static final byte[] encryptedLocationData =
		new byte[]{(byte) 0xfe, (byte) 0xdc, (byte) 0xba, (byte) 0x98, 0x76, 0x54, 0x32, 0x10};
	private static final HashedMacAddress hashedMacAddress =
		HashedMacAddress.fromString("0123456789ABCDEF0123456789ABCDEF");
	private static UserData userData;
	private static RequestManager requestManager;
	private static ScanResultToUpload scanResultToUpload;

	@Mock
	ScanResultToUploadStore scanResultToUploadStore;

	@Before
	public void setUp(){
		MockitoAnnotations.initMocks(this);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		preferences.edit().putString("name", "User").apply();
		preferences.edit().putString("cookie", "UserCookie").apply();
		preferences.edit().putString("localKey", "password").apply();
		userData = new UserData(preferences);
		requestManager = new RequestManager(userData);
		scanResultToUpload = new ScanResultToUpload(hashedMacAddress, encryptedLocationData);
		scanResultUploadManager = new ScanResultUploadManager(scanResultToUploadStore,requestManager);
	}

	@Test
	public void initialTest(){
		Assert.assertNotNull(scanResultUploadManager);
	}

	@Test
	public void triggerUploadTest_1(){
		scanResultUploadManager.triggerUpload();
	}

	@Test
	public void onReceiveScanResultUploadResultTest(){
		scanResultUploadManager.onReceiveScanResultUploadResult();
	}

	@Test
	public void triggererUploadTest_2() {
		ScanResultToUpload scanResultToUpload = new ScanResultToUpload(hashedMacAddress,encryptedLocationData);
		when(scanResultToUploadStore.getOldest()).thenReturn(scanResultToUpload, null);
		scanResultUploadManager.triggerUpload();
	}
}
