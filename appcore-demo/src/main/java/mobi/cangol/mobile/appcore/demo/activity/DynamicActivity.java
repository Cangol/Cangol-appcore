package mobi.cangol.mobile.appcore.demo.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.appcore.demo.R;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.route.RouteService;
import mobi.cangol.mobile.stat.StatAgent;


public class DynamicActivity extends AppCompatActivity {
    private static final String TAG = "DynamicActivity";
    private RouteService mRouteService;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRouteService= (RouteService) ((CoreApplication)getApplication()).getAppService(AppService.ROUTE_SERVICE);
        handleIntent(getIntent());
        ((CoreApplication)getApplication()).addActivityToManager(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.setActionBar(new Toolbar(this));
            this.getActionBar().setDisplayHomeAsUpEnabled(true);
        }
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

    protected void handleIntent(Intent intent) {
        Uri data = intent.getData();
        if(data!=null){
            mRouteService.handleIntent(this,intent);
        }else{
            String className = intent.getStringExtra("class");
            Bundle bundle = intent.getBundleExtra("args");
            try {
                toFragment((Class<? extends Fragment>) Class.forName(className), bundle,false);
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
    public void toFragment(Class<? extends Fragment> fragmentClass, Bundle bundle, boolean newStack) {
        Log.i("fragment ");
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
