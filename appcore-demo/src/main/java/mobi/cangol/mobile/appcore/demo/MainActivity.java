package mobi.cangol.mobile.appcore.demo;


import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.utils.DeviceInfo;

public class MainActivity extends FragmentActivity {
    private static List<Class<? extends Fragment>> fragments=new ArrayList<Class<? extends Fragment>>();
    static {
        fragments.add(AppServiceFragment.class);
        fragments.add(DatabaseFragment.class);
        fragments.add(HttpFragment.class);
        fragments.add(LoggingFragment.class);
        fragments.add(ParserFragment.class);
        fragments.add(SecurityFragment.class);
        fragments.add(SoapFragment.class);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            FragmentManager fm = this.getSupportFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.framelayout, new DemoListFragment(fragments), null)
                    .commit();
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

    @SuppressLint("ValidFragment")
    class DemoListFragment extends ListFragment {
        private List<Class<? extends Fragment>> list;
        public DemoListFragment(List<Class<? extends Fragment>> list){
            this.list=list;
        }
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            List<String> listStr=new ArrayList<String>();
            for (int i = 0; i < list.size(); i++) {
                listStr.add(list.get(i).getSimpleName().replace("Fragment",""));
            }
            setListAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, listStr));
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            super.onListItemClick(l, v, position, id);
            getActivity().setTitle((String)getListAdapter().getItem(position));
            toFragment(list.get(position));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        @Override
        public void onStart() {
            super.onStart();
            if(this.getFragmentManager().getBackStackEntryCount()==0){
                getActivity().setTitle(R.string.app_name);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    getActionBar().setDisplayHomeAsUpEnabled(false);
                }
            }
        }
    }
}

