package mobi.cangol.mobile.appcore.demo.fragment.appservice;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Random;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.appcore.demo.R;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.route.RouteService;
import mobi.cangol.mobile.stat.StatAgent;

/**
 * Created by weixuewu on 16/4/30.
 */
public class RouteServiceFragment extends Fragment {
    private static final String TAG = "RouteServiceFragment";
    private RouteService routeService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        routeService = ((CoreApplication) this.getActivity().getApplicationContext()).getAppService(AppService.ROUTE_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_service_route, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        getView().findViewById(R.id.button0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routeService.build("lib/test1")
                        .putString("key","hello "+new Random().nextInt(100))
                        .navigation(getContext());
            }
        });
        getView().findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routeService.build("lib/test")
                        .putString("key","hello "+new Random().nextInt(100))
                        .navigation(getContext());
            }
        });

        getView().findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routeService.build("lib/test")
                        .putString("key","newStack "+new Random().nextInt(100))
                        .navigation(getContext(),true);
            }
        });

        getView().findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("appdemo://app/test?key=actionView "+new Random().nextInt(100)));
                startActivity(intent);
            }
        });
        getView().findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("appdemo://lib/test?key=actionView "+new Random().nextInt(100)));
                startActivity(intent);
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
