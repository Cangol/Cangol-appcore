package mobi.cangol.mobile.appcore.demo;


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

import mobi.cangol.mobile.appcore.demo.fragment.AnalyticsServiceFragment;
import mobi.cangol.mobile.appcore.demo.fragment.CacheManagerFragment;
import mobi.cangol.mobile.appcore.demo.fragment.ConfigServiceFragment;
import mobi.cangol.mobile.appcore.demo.fragment.CrashServiceFragment;
import mobi.cangol.mobile.appcore.demo.fragment.DownloadManagerFragment;
import mobi.cangol.mobile.appcore.demo.fragment.LocationServiceFragment;
import mobi.cangol.mobile.appcore.demo.fragment.SessionServiceFragment;
import mobi.cangol.mobile.appcore.demo.fragment.StatusServiceFragment;
import mobi.cangol.mobile.appcore.demo.fragment.UpgradeServiceFragment;

public class MainActivity extends FragmentActivity {
    private static List<Class<? extends Fragment>> fragments=new ArrayList<Class<? extends Fragment>>();
    static {
        fragments.add(AnalyticsServiceFragment.class);
        fragments.add(CacheManagerFragment.class);
        fragments.add(ConfigServiceFragment.class);
        fragments.add(CrashServiceFragment.class);
        fragments.add(DownloadManagerFragment.class);
        fragments.add(LocationServiceFragment.class);
        fragments.add(SessionServiceFragment.class);
        fragments.add(StatusServiceFragment.class);
        fragments.add(UpgradeServiceFragment.class);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        if (savedInstanceState == null) {
            FragmentManager fm = this.getSupportFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.framelayout, new DemoListFragment(), null)
                    .addToBackStack("DemoListFragment")
                    .commit();
        }

    }


    class DemoListFragment extends ListFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            List<String> list=new ArrayList<String>();
            for (int i = 0; i < fragments.size(); i++) {
                list.add(fragments.get(i).getSimpleName().replace("Fragment",""));
            }
            setListAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, list));
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            super.onListItemClick(l, v, position, id);
            getActivity().setTitle((String)getListAdapter().getItem(position));
            toFragment(fragments.get(position));
        }

        private void toFragment(Class<? extends Fragment> fragmentClass) {
            FragmentManager fm = this.getFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.framelayout, Fragment.instantiate(this.getActivity(), fragmentClass.getName(), null))
                    .addToBackStack(fragmentClass.getName())
                    .commit();
        }

        @Override
        public void onStart() {
            super.onStart();
            getActivity().setTitle(R.string.app_name);
        }
    }
}

