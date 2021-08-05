package mobi.cangol.mobile.appcore.demo.activity;


import android.content.Intent;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.appcore.demo.fragment.MainFragment;
import mobi.cangol.mobile.appcore.demo.R;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.route.OnNavigation;
import mobi.cangol.mobile.service.route.RouteService;
import mobi.cangol.mobile.stat.StatAgent;
import mobi.cangol.mobile.utils.DeviceInfo;

public class MainActivity extends AppCompatActivity implements OnNavigation {
    private static final String TAG = "MainActivity";
    private RouteService mRouteService;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            toFragment(MainFragment.class,null,false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.setActionBar(new Toolbar(this));
        }
        Log.d("getMD5Fingerprint="+DeviceInfo.getMD5Fingerprint(this));
        Log.d("getSHA1Fingerprint="+DeviceInfo.getSHA1Fingerprint(this));
        mRouteService= (RouteService) ((CoreApplication)getApplication()).getAppService(AppService.ROUTE_SERVICE);
        mRouteService.registerNavigation(this);
        test();
        ((CoreApplication)getApplication()).addActivityToManager(this);
        Log.d("ActivityManager="+((CoreApplication)getApplication()).getActivityManager().size());
        handleIntent(getIntent());
    }
    protected void handleIntent(Intent intent) {
        Uri data = intent.getData();
        if(data!=null){
            mRouteService.handleIntent(this,intent);
        }

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
        }else{
            super.onBackPressed();
        }
        return true;
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
    public void notFound(String path) {
        Toast.makeText(this, path+" not found!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toFragment(Class<? extends Fragment> fragmentClass, Bundle bundle, boolean newStack) {
        Log.i("toFragment ");
        if(newStack){
            Intent intent = new Intent(this, DynamicActivity.class);
            intent.putExtra("class", fragmentClass.getName());
            intent.putExtra("args", bundle);
            startActivity(intent);
        }else{
            FragmentManager fm = this.getSupportFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.framelayout, Fragment.instantiate(this, fragmentClass.getName(), bundle))
                    .addToBackStack(fragmentClass.getName())
                    .commit();
        }
    }
}

