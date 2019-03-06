package mobi.cangol.mobile.appcore.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import mobi.cangol.mobile.logging.Log;


public class DynamicActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleIntent(getIntent());
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
            super.onBackPressed();
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = this.getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 1) {
            super.onBackPressed();
        } else {
            finish();
        }
    }

    protected void toFragment(Class fragmentClass, Bundle bundle) {
        FragmentManager fm = this.getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.framelayout, Fragment.instantiate(this, fragmentClass.getName(), bundle))
                .addToBackStack(fragmentClass.getName())
                .commit();
    }
}
