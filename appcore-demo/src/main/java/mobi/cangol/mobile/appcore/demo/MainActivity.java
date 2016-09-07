package mobi.cangol.mobile.appcore.demo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.utils.DeviceInfo;

public class MainActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            toFragment(MainFragment.class);
        }
        Log.d("getMD5Fingerprint="+DeviceInfo.getMD5Fingerprint(this));
    }

    @Override
    public boolean onNavigateUp() {
        FragmentManager fm = this.getSupportFragmentManager();
        if(fm.getBackStackEntryCount()>0){
            fm.popBackStack();
            return true;
        }
        return super.onNavigateUp();
    }
    protected void toFragment(Class<? extends Fragment> fragmentClass) {
        FragmentManager fm = this.getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.framelayout, Fragment.instantiate(this, fragmentClass.getName(), null))
                .addToBackStack(fragmentClass.getName())
                .commit();
    }
}

