package mobi.cangol.mobile.appcore.demo.fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import mobi.cangol.mobile.appcore.demo.activity.DynamicActivity;
import mobi.cangol.mobile.appcore.demo.activity.MainActivity;
import mobi.cangol.mobile.appcore.demo.fragment.appservice.AnalyticsServiceFragment;
import mobi.cangol.mobile.appcore.demo.fragment.appservice.CacheManagerFragment;
import mobi.cangol.mobile.appcore.demo.fragment.appservice.ConfigServiceFragment;
import mobi.cangol.mobile.appcore.demo.fragment.appservice.CrashServiceFragment;
import mobi.cangol.mobile.appcore.demo.fragment.appservice.DownloadManagerFragment;
import mobi.cangol.mobile.appcore.demo.fragment.appservice.LocationServiceFragment;
import mobi.cangol.mobile.appcore.demo.fragment.appservice.OberserverManagerFragment;
import mobi.cangol.mobile.appcore.demo.fragment.appservice.RouteServiceFragment;
import mobi.cangol.mobile.appcore.demo.fragment.appservice.SessionServiceFragment;
import mobi.cangol.mobile.appcore.demo.fragment.appservice.StatusServiceFragment;
import mobi.cangol.mobile.appcore.demo.fragment.appservice.UpgradeServiceFragment;
import mobi.cangol.mobile.stat.StatAgent;

/**
 * Created by weixuewu on 16/4/30.
 */
public class AppServiceFragment extends ListFragment {
    private static final String TAG = "AppServiceFragment";
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
        list.add(OberserverManagerFragment.class);
        list.add(RouteServiceFragment.class);
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
        if(getActivity() instanceof MainActivity){
            ((MainActivity)getActivity()).toFragment(list.get(position),new Bundle(),false);
        }else{
            ((DynamicActivity)getActivity()).toFragment(list.get(position),new Bundle(),false);
        }
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle("AppService");
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public void onPause() {
        super.onPause();
        StatAgent.getInstance().onFragmentPause(TAG);
    }

    @Override
    public void onResume() {
        super.onResume();
        StatAgent.getInstance().onFragmentResume(TAG);
    }
}
