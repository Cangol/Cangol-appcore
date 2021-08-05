package mobi.cangol.mobile.appcore.demo.fragment.appservice;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.appcore.demo.R;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.crash.CrashReportListener;
import mobi.cangol.mobile.service.crash.CrashService;
import mobi.cangol.mobile.stat.StatAgent;

/**
 * Created by weixuewu on 16/4/30.
 */
public class CrashServiceFragment extends Fragment{
    private static final String TAG = "CrashServiceFragment";
    private CrashService crashService;
    private Button button1;
    private TextView textView1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crashService = (CrashService) ((CoreApplication) this.getActivity().getApplicationContext()).getAppService(AppService.CRASH_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_service_crash, container, false);
        return v;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
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
    private void initViews(){
        textView1 = this.getView().findViewById(R.id.textView1);
        button1 = this.getView().findViewById(R.id.button1);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                throw new NullPointerException("crash test");
            }
        });
        crashService.report(new CrashReportListener(){

            @Override
            public void report(String path,String error,String position,String context,String timestamp,String fatal) {
                updateViews(path,error,position,context,timestamp,fatal);
            }

        });
    }
    private void updateViews(String path,String error,String position,String context,String timestamp,String fatal) {
        textView1.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView1.setText("--------------crash---------------");
        textView1.append("\n\npath=" + path);
        textView1.append("\n\nposition=" + position);
        textView1.append("\n\ntimestamp=" + timestamp);
        textView1.append("\n\nfatal=" + fatal);
        textView1.append("\n\nerror=" + error);
        textView1.append("\n\ncontext=" + context);

        Log.d(textView1.getText().toString());
    }
}
