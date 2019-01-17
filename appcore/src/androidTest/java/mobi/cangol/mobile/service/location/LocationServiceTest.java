package mobi.cangol.mobile.service.location;

import android.location.Location;
import android.test.ApplicationTestCase;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.service.AppService;

/**
 * Created by weixuewu on 16/6/11.
 */
public class LocationServiceTest extends ApplicationTestCase<CoreApplication> {
    private static final String TAG = "LocationService";
    private CoreApplication coreApplication;
    private LocationService locationService;

    public LocationServiceTest() {
        super(CoreApplication.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        createApplication();
        coreApplication = getApplication();
        locationService = (LocationService) coreApplication.getAppService(AppService.LOCATION_SERVICE);
    }

    public void testRequestLocationUpdates() {
        //locationService.requestLocationUpdates();
    }

    public void testRemoveLocationUpdates() {
        locationService.removeLocationUpdates();
    }

    public void testGetLastKnownLocation() {
        locationService.getLastKnownLocation();
    }

    public void testIsBetterLocation() {
        locationService.isBetterLocation(new Location(TAG));
    }

    public void testSetBetterLocationListener() {
        locationService.setBetterLocationListener(new BetterLocationListener() {
            @Override
            public void onBetterLocation(Location mLocation) {

            }

            @Override
            public void timeout(Location mLocation) {

            }
        });
    }

    public void testGetAddress() {
        locationService.getAddress();
    }
}