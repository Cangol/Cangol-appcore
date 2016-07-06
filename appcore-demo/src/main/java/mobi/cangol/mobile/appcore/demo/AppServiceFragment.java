package mobi.cangol.mobile.appcore.demo;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import mobi.cangol.mobile.appcore.demo.appservice.AnalyticsServiceFragment;
import mobi.cangol.mobile.appcore.demo.appservice.CacheManagerFragment;
import mobi.cangol.mobile.appcore.demo.appservice.ConfigServiceFragment;
import mobi.cangol.mobile.appcore.demo.appservice.CrashServiceFragment;
import mobi.cangol.mobile.appcore.demo.appservice.DownloadManagerFragment;
import mobi.cangol.mobile.appcore.demo.appservice.LocationServiceFragment;
import mobi.cangol.mobile.appcore.demo.appservice.SessionServiceFragment;
import mobi.cangol.mobile.appcore.demo.appservice.StatusServiceFragment;
import mobi.cangol.mobile.appcore.demo.appservice.UpgradeServiceFragment;

/**
 * Created by weixuewu on 16/4/30.
 */
public class AppServiceFragment extends ListFragment {
    private List<Class<? extends Fragment>> list;
    public AppServiceFragment(){
        this.list=new ArrayList<Class<? extends Fragment>>();
        list.add(AnalyticsServiceFragment.class);
        list.add(CacheManagerFragment.class);
        list.add(ConfigServiceFragment.class);
        list.add(CrashServiceFragment.class);
        list.add(LocationServiceFragment.class);
        list.add(SessionServiceFragment.class);
        list.add(StatusServiceFragment.class);
        list.add(UpgradeServiceFragment.class);
        list.add(DownloadManagerFragment.class);
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        getActivity().setTitle((String)getListAdapter().getItem(position));
        ((MainActivity)getActivity()).toFragment(list.get(position));
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle("AppService");
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
