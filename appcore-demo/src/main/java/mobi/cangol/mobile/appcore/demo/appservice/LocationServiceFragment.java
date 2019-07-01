package mobi.cangol.mobile.appcore.demo.appservice;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.appcore.demo.R;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.location.BetterLocationListener;
import mobi.cangol.mobile.service.location.LocationService;

/**
 * Created by weixuewu on 16/4/30.
 */
public class LocationServiceFragment extends Fragment{

    private LocationService locationService;
    private TextView textView1;
    private Button button1, button2,button3,button4;
    private boolean isShowing;
    private Toast toast;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationService = (LocationService) ((CoreApplication) this.getActivity().getApplicationContext()).getAppService(AppService.LOCATION_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_service_location, container, false);
        return v;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        //updateViews();
        toast=Toast.makeText(getActivity(),"定位中...",Toast.LENGTH_LONG);
    }
    private void showToast() {
        isShowing=true;
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){

            @Override
            public void run() {
                while(isShowing){
                    toast.show();
                }
            }
        },10);
    }
    private void hideToast() {
        isShowing=false;
    }
    private void initViews(){
        textView1 = this.getView().findViewById(R.id.textView1);
        button1 = this.getView().findViewById(R.id.button1);
        button2 = this.getView().findViewById(R.id.button2);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationService.requestLocationUpdates(getActivity());
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationService.removeLocationUpdates();
                hideToast();
            }
        });
        locationService.setBetterLocationListener(new BetterLocationListener() {

            @Override
            public void needPermission(String[] permissions) {
                ActivityCompat.requestPermissions(getActivity(), permissions, 200);
            }

            @Override
            public void providerDisabled(String provider) {
                Log.d("providerDisabled "+provider);
                Toast.makeText(getContext(),provider,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBetterLocation(Location location) {
                Log.d("onBetterLocation");
                if(getActivity()!=null){
                    updateViews();
                    hideToast();
                }
            }

            @Override
            public void timeout(Location location) {
                Log.d("timeout");
                if(getActivity()!=null){
                    updateViews();
                    hideToast();
                }
            }

            @Override
            public void positioning() {
                showToast();
            }
        });
    }
    private void updateViews(){
        textView1.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView1.setText("--------------Location---------------");
        textView1.append("\ngetLastKnownLocation=" + locationService.getLastKnownLocation());
        textView1.append("\ngetAddress=" + locationService.getLastKnownLocation());

        Log.d(textView1.getText().toString());
    }

    @Override
    public void onDestroyView() {
        hideToast();
        super.onDestroyView();
    }
}
