package mobi.cangol.mobile.appcore.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.stat.StatAgent;


public class DynamicActivity extends FragmentActivity {
    private static final String TAG = "DynamicActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleIntent(getIntent());
        ((CoreApplication)getApplication()).addActivityToManager(this);
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onDestroy() {
        ((CoreApplication)getApplication()).delActivityFromManager(this);
        super.onDestroy();
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    protected void handleIntent(Intent intent) {

        Uri data = intent.getData();
        if(data!=null){
            Log.i("data="+data+",host="+data.getHost()+",path="+data.getPath());
        }else{
            String className = intent.getStringExtra("class");
            Bundle bundle = intent.getBundleExtra("args");
            try {
                toFragment(Class.forName(className), bundle);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onNavigateUp() {
        FragmentManager fm = this.getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 1) {
            fm.popBackStack();
            return true;
        } else {
            finish();
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = this.getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 1) {
            fm.popBackStack();
        } else {
            finish();
        }
    }

    public void toFragment(Class fragmentClass, Bundle bundle) {
        FragmentManager fm = this.getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.framelayout, Fragment.instantiate(this, fragmentClass.getName(), bundle))
                .addToBackStack(fragmentClass.getName())
                .commit();
    }


}
