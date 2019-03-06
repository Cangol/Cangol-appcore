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

import java.util.concurrent.atomic.AtomicInteger;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.appcore.demo.R;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.analytics.AnalyticsService;
import mobi.cangol.mobile.service.analytics.IMapBuilder;
import mobi.cangol.mobile.service.analytics.ITracker;
import mobi.cangol.mobile.utils.TimeUtils;

/**
 * Created by weixuewu on 16/4/30.
 */
public class AnalyticsServiceFragment extends Fragment{

    private AnalyticsService analyticsService;
    private ITracker tracker;
    private TextView textView1;
    private Button button1,button2,button3,button4;
    private final AtomicInteger atomicInteger = new AtomicInteger(10000);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        analyticsService = (AnalyticsService) ((CoreApplication) this.getActivity().getApplicationContext()).getAppService(AppService.ANALYTICS_SERVICE);
        analyticsService.setDebug(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_service_analytics, container, false);
        return v;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }
    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
    private void initViews(){
        textView1 = this.getView().findViewById(R.id.textView1);
        button1 = this.getView().findViewById(R.id.button1);
        button2 = this.getView().findViewById(R.id.button2);
        button3 = this.getView().findViewById(R.id.button3);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tracker=analyticsService.getTracker("ITracker"+atomicInteger.getAndIncrement());
                updateViews("\n"+tracker.getTrackingId()+" get");
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tracker==null){
                    showToast("Please getTracker");
                }else{
                    if(tracker.isClosed()){
                        updateViews("\n"+tracker.getTrackingId()+" is closed");
                    }else{
                        updateViews("\n"+tracker.getTrackingId()+" close");
                        analyticsService.closeTracker(tracker.getTrackingId());
                    }
                }
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tracker==null){
                    showToast("Please getTracker");
                }else{
                    IMapBuilder builder=IMapBuilder.build();
                    builder.setUrl("http://www.cangol.mobi/cmweb/api/countly/event.do");
                    builder.set("id","1")
                            .set("name","Jick")
                            .set("age","24")
                            .set("timestamp", TimeUtils.getCurrentTime());
                    boolean result=tracker.send(builder);
                    updateViews("\n"+tracker.getTrackingId()+" send "+result);
                }
            }
        });
        textView1.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView1.setText("--------------analytics---------------");
    }
    private void updateViews(String message) {
        textView1.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView1.append(message);
        Log.d(message);
    }
}
