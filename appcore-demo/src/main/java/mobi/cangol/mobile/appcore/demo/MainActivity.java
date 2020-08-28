package mobi.cangol.mobile.appcore.demo;


import android.content.Intent;
import android.net.ParseException;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

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
        test();
        ((CoreApplication)getApplication()).addActivityToManager(this);
        Log.d("ActivityManager="+((CoreApplication)getApplication()).getActivityManager().size());
    }
    public void test(){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Log.e(TAG,Build.VERSION.SDK_INT+" test "+  new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse("2019-09-19T04:00:40+00:00").toString());
            }else{
                Log.e(TAG,Build.VERSION.SDK_INT+ " test"+ new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse("2019-09-19T04:00:40+00:00").toString());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
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
    protected void toFragmentByDynamicActivity(Class<? extends Fragment> fragmentClass) {
        Intent intent = new Intent(this, DynamicActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtra("class", fragmentClass.getName());
        intent.putExtra("args", bundle);
        startActivity(intent);
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

