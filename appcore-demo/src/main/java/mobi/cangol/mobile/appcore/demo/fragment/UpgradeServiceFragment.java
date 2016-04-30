package mobi.cangol.mobile.appcore.demo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.appcore.demo.R;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.upgrade.UpgradeListener;
import mobi.cangol.mobile.service.upgrade.UpgradeService;

/**
 * Created by weixuewu on 16/4/30.
 */
public class UpgradeServiceFragment extends Fragment{
    private String url="http://180.153.105.145/dd.myapp.com/16891/8E5A9885970F76080F8445C652DE347C.apk?mkey=5715c34fc20a8141&f=d511&fsname=com.tencent.mobileqq_6.3.1_350.apk&p=.apk";
    private UpgradeService upgradeService;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        upgradeService= (UpgradeService) ((CoreApplication) this.getActivity().getApplicationContext()).getAppService(AppService.UPGRADE_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_upgrade_service, container, false);
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
                upgradeService.upgradeApk("QQ",url,false);
            }
        });
        getView().findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //upgradeService.cancel(url);
            }
        });
        upgrade();
    }
    private void upgrade(){
        upgradeService.setOnUpgradeListener(new UpgradeListener(){

            @Override
            public void upgrade(boolean constraint) {

            }

            @Override
            public void onFinish() {

            }
        });
    }
}
