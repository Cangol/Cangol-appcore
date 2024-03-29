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

package mobi.cangol.mobile.appcore.demo.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import java.util.ArrayList;
import java.util.List;

import mobi.cangol.mobile.appcore.demo.R;
import mobi.cangol.mobile.appcore.demo.activity.MainActivity;
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
        getActivity().setTitle((String)getListAdapter().getItem(position));
        ((MainActivity)getActivity()).toFragment(fragments.get(position),new Bundle(),false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
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
