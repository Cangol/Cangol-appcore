package mobi.cangol.mobile.appcore.demo;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
