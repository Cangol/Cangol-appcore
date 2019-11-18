package mobi.cangol.mobile.appcore.demo.appservice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.appcore.demo.R;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.upgrade.OnUpgradeListener;
import mobi.cangol.mobile.service.upgrade.UpgradeService;
import mobi.cangol.mobile.stat.StatAgent;

/**
 * Created by weixuewu on 16/4/30.
 */
public class UpgradeServiceFragment extends Fragment{
    private static final String TAG = "UpgradeServiceFragment";
    private String url="https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
    private UpgradeService upgradeService;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        upgradeService= (UpgradeService) ((CoreApplication) this.getActivity().getApplicationContext()).getAppService(AppService.UPGRADE_SERVICE);
        upgradeService.setDebug(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_service_upgrade, container, false);
        return v;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }
    private void initViews(){
        getView().findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upgradeService.upgrade("QQ.apk",url,true,true);
            }
        });
        getView().findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upgradeService.cancel("QQ.apk");
            }
        });
        upgradeService.setOnUpgradeListener("QQ.apk",new OnUpgradeListener(){

            @Override
            public void upgrade(boolean force) {

            }

            @Override
            public void progress(int speed, int progress) {

            }

            @Override
            public void onFinish(String filePath) {

            }

            @Override
            public void onFailure(String error) {

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
