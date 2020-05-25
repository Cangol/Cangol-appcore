/*
 *
 *  Copyright (c) 2013 Cangol
 *   <p/>
 *   Licensed under the Apache License, Version 2.0 (the "License")
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *  <p/>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p/>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package mobi.cangol.mobile.appcore.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.appcore.libdemo.LibTestFragment;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.route.RouteService;
import mobi.cangol.mobile.stat.StatAgent;

/**
 * Created by xuewu.wei on 2016/8/31.
 */
public class MainFragment extends ListFragment {
    private static final String TAG="MainFragment";
    private static List<Class<? extends Fragment>> fragments=new ArrayList<Class<? extends Fragment>>();
    static {
        fragments.add(AppServiceFragment.class);
        fragments.add(DatabaseFragment.class);
        fragments.add(HttpFragment.class);
        fragments.add(LoggingFragment.class);
        fragments.add(ParserFragment.class);
        fragments.add(SecurityFragment.class);
        fragments.add(SoapFragment.class);
        fragments.add(UtilsFragment.class);
        fragments.add(StatFragment.class);
        fragments.add(LibTestFragment.class);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List<String> listStr=new ArrayList<String>();
        for (int i = 0; i < fragments.size(); i++) {
            listStr.add(fragments.get(i).getSimpleName().replace("Fragment",""));
        }
        setListAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, listStr));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(position>=0){
            getActivity().setTitle((String)getListAdapter().getItem(position));
            ((MainActivity)getActivity()).toFragmentByDynamicActivity(fragments.get(position));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }else{
            RouteService mRouteService= (RouteService) ((CoreApplication)getActivity().getApplication()).getAppService(AppService.ROUTE_SERVICE);
            mRouteService.build("lib")
                    .putString("key","hello "+new Random().nextInt(100))
                    .navigation(this.getContext());

            Intent intent=new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("app://main/lib"));
            intent.putExtra("class","");
            Bundle bundle=new Bundle();
            bundle.putString("key","hello "+new Random().nextInt(100));
            intent.putExtra("args",bundle);
            startActivity(intent);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        if(this.getFragmentManager().getBackStackEntryCount()==0){
            getActivity().setTitle(R.string.app_name);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
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
