package mobi.cangol.mobile.appcore.demo;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.appcore.libdemo.LibTestFragment;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.route.OnNavigation;
import mobi.cangol.mobile.service.route.RouteService;
import mobi.cangol.mobile.stat.StatAgent;
import mobi.cangol.mobile.utils.DeviceInfo;

//import com.testfairy.TestFairy;

public class MainActivity extends FragmentActivity implements OnNavigation {
    private static final String TAG = "MainActivity";
    private RouteService mRouteService;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            toFragment(MainFragment.class);
        }
        //TestFairy.begin(this, "47ea7365f8383a1cb728d0f84a918503f8acaae4");
        Log.d("getMD5Fingerprint="+DeviceInfo.getMD5Fingerprint(this));
        Log.d("getSHA1Fingerprint="+DeviceInfo.getSHA1Fingerprint(this));
        mRouteService= (RouteService) ((CoreApplication)getApplication()).getAppService(AppService.ROUTE_SERVICE);
        mRouteService.registerNavigation(this);
        mRouteService.register("lib",LibTestFragment.class);
    }

    @Override
    public boolean onNavigateUp() {
        FragmentManager fm = this.getSupportFragmentManager();
        if(fm.getBackStackEntryCount()>1){
            fm.popBackStack();
            return true;
        }else{
            super.onBackPressed();
            return true;
        }
    }
    @Override
    public void onBackPressed() {
        FragmentManager fm = this.getSupportFragmentManager();
        if(fm.getBackStackEntryCount()>1){
            super.onBackPressed();
        }else{
            finish();
        }
    }
    protected void toFragment(Class<? extends Fragment> fragmentClass) {
        FragmentManager fm = this.getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.framelayout, Fragment.instantiate(this, fragmentClass.getName(), null))
                .addToBackStack(fragmentClass.getName())
                .commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatAgent.getInstance().onActivityPause(TAG);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatAgent.getInstance().onFragmentPause(TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((CoreApplication)getApplication()).exit();
    }

    @Override
    public void toActivity(Intent intent) {
        Log.i("activity ");
        startActivity(intent);
    }

    @Override
    public void toFragment(Fragment fragment) {
        Log.i("fragment ");
        FragmentManager fm = this.getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.framelayout, fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();
    }
}

