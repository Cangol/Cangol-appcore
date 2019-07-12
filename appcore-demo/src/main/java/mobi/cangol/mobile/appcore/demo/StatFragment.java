package mobi.cangol.mobile.appcore.demo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.analytics.AnalyticsService;
import mobi.cangol.mobile.soap.SoapClient;
import mobi.cangol.mobile.soap.SoapResponseHandler;
import mobi.cangol.mobile.stat.StatAgent;
import mobi.cangol.mobile.utils.TimeUtils;

/**
 * Created by weixuewu on 16/4/30.
 */
public class StatFragment extends Fragment {

    private static final String TAG="StatFragment";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatAgent.getInstance().setDebug(true);
        //StatAgent.getInstance().setStatServerURL("http://192.168.57.171/");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stat, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }
    private void initViews() {
        getView().findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatAgent.getInstance().send(StatAgent.Builder.createAppView(TAG));
            }
        });
        getView().findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatAgent.getInstance().send(StatAgent.Builder.createException("test", "1", "test", TimeUtils.getCurrentTime(), "1"));
            }
        });
        getView().findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatAgent.getInstance().send(StatAgent.Builder.createEvent("test", TAG, "test", null, null));
            }
        });
        getView().findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatAgent.getInstance().send(StatAgent.Builder.createTiming(TAG, 1000L));
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
}
