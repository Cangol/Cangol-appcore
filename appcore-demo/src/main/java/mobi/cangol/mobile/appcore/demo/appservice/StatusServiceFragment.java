package mobi.cangol.mobile.appcore.demo.appservice;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.appcore.demo.R;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.status.StatusListener;
import mobi.cangol.mobile.service.status.StatusService;
import mobi.cangol.mobile.stat.StatAgent;

/**
 * Created by weixuewu on 16/4/30.
 */
public class StatusServiceFragment extends Fragment {
    private static final String TAG = "StatusServiceFragment";
    private StatusService statusService;
    private TextView textView1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusService = (StatusService) ((CoreApplication) this.getActivity().getApplicationContext()).getAppService(AppService.STATUS_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_service_status, container, false);
        return v;
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initData();
    }

    private void initViews() {
        textView1 = this.getView().findViewById(R.id.textView1);
    }
    private void updateViews(){
        textView1.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView1.setText("isConnection:      " + statusService.isConnection());
        textView1.append("\nisWifiConnection:  " + statusService.isWifiConnection());
        textView1.append("\nisNetworkLocation: " + statusService.isNetworkLocation());
        textView1.append("\nisGPSLocation:     " + statusService.isGPSLocation());
        textView1.append("\nisCallingState:    " + statusService.isCallingState());

        Log.d(textView1.getText().toString());
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
    private void initData() {
        statusService.registerStatusListener(new StatusListener() {
            @Override
            public void networkConnect(Context context) {
                showToast("networkConnect");
                updateViews();
            }

            @Override
            public void networkDisconnect(Context context) {
                showToast("networkDisconnect");
                updateViews();
            }

            @Override
            public void networkTo3G(Context context) {
                showToast("networkTo3G");
                updateViews();
            }

            @Override
            public void storageRemove(Context context) {
                showToast("storageRemove");
                updateViews();
            }

            @Override
            public void storageMount(Context context) {
                showToast("storageMount");
                updateViews();
            }

            @Override
            public void callStateIdle() {
                showToast("callStateIdle");
                updateViews();
            }

            @Override
            public void callStateOffhook() {
                showToast("callStateOffhook");
                updateViews();
            }

            @Override
            public void callStateRinging() {
                showToast("callStateRinging");
                updateViews();
            }
        });
    }
}
