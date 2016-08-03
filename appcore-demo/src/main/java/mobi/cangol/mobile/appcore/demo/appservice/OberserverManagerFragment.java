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

package mobi.cangol.mobile.appcore.demo.appservice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.appcore.demo.R;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.event.ObserverManager;
import mobi.cangol.mobile.service.event.Subscribe;

/**
 * Created by weixuewu on 16/4/30.
 */
public class OberserverManagerFragment extends Fragment{

    private ObserverManager observerManager;
    private TextView textView1;
    private Button button1, button2;
    private Toast toast;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        observerManager = (ObserverManager) ((CoreApplication) this.getActivity().getApplicationContext()).getAppService(AppService.OBSERVER_MANAGER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_service_oberserver, container, false);
        return v;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }
    private void initViews(){
        textView1 = (TextView) this.getView().findViewById(R.id.textView1);
        button1 = (Button) this.getView().findViewById(R.id.button1);
        button2 = (Button) this.getView().findViewById(R.id.button2);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateViews("Click button1 ");
                observerManager.post("button1Click",null);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateViews("Click button2 ");
                observerManager.post("button2Click","hello world");
            }
        });
        textView1.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView1.setText("--------------ObserverManager---------------");
        observerManager.register(this);
    }
    @Subscribe("button1Click")
    public void onEvent1(String msg){
        updateViews("onEvent1 "+msg);
    }
    @Subscribe("button2Click")
    public void onEvent2(String msg){
        updateViews("onEvent2 "+msg);
    }
    private void updateViews(String message){
        textView1.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView1.append("\n"+message);
        Log.d(message);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        observerManager.unregister(this);
    }
}
