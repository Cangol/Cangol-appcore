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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.appcore.demo.R;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.session.Session;
import mobi.cangol.mobile.service.session.SessionService;
import mobi.cangol.mobile.stat.StatAgent;

/**
 * Created by weixuewu on 16/4/30.
 */
public class SessionServiceFragment extends Fragment{
    private static final String TAG = "SessionServiceFragment";
    private SessionService sessionService;
    private TextView textView1;
    private Button button1, button2,button3,button33,button4,button5;
    private Session mUserSession;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionService = (SessionService) ((CoreApplication) this.getActivity().getApplicationContext()).getAppService(AppService.SESSION_SERVICE);
        mUserSession=sessionService.getUserSession("test");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_service_session, container, false);
        return v;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        updateViews();
    }
    private void initViews(){
        textView1 = this.getView().findViewById(R.id.textView1);
        button1 = this.getView().findViewById(R.id.button1);
        button2 = this.getView().findViewById(R.id.button2);
        button3 = this.getView().findViewById(R.id.button3);
        button33= this.getView().findViewById(R.id.button33);
        button4 = this.getView().findViewById(R.id.button4);
        button5 = this.getView().findViewById(R.id.button5);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionService.put(TAG,1);
                updateViews();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionService.put(TAG,new User(1,"Rose","18"));
                updateViews();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("id",1);
                    jsonObject.put("name","Rose");
                    jsonObject.put("age","18");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sessionService.saveJSONObject(TAG,jsonObject);
                updateViews();
            }
        });
        button33.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONArray jsonArray=new JSONArray();
                for (int i = 0; i < 4; i++) {
                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("id",i);
                        jsonObject.put("name","Rose"+i);
                        jsonObject.put("age","18");
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                sessionService.saveJSONArray(TAG,jsonArray);
                sessionService.refresh();
                updateViews();
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionService.remove(TAG);
                updateViews();
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionService.clearAll();
                updateViews();
            }
        });
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
    private void updateViews() {
        textView1.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView1.setText("--------------session---------------");
        textView1.append("\n containsKey=" + sessionService.containsKey(TAG));
        textView1.append("\n value=" + sessionService.get(TAG));

        Log.d(textView1.getText().toString());
    }
}
