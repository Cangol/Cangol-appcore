package mobi.cangol.mobile.appcore.demo.fragment.appservice;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
    private String url="http://music-hotpot.oss-cn-hongkong.aliyuncs.com/songs/803263126016617416171574.mp3";
    //private String url="http://180.153.105.145/dd.myapp.com/16891/8E5A9885970F76080F8445C652DE347C.apk?mkey=5715c34fc20a8141&f=d511&fsname=com.tencent.mobileqq_6.3.1_350.apk&p=.apk";
    //private String url="https://pro-app-qn.fir.im/d5852a8288097d15e89c75a865d16e280ed91fb0.apk?attname=FR_2.2.0_build18_re937f380_20171011_alpha.apk_2.2.0.apk&e=1507694083&token=LOvmia8oXF4xnLh0IdH05XMYpH6ENHNpARlmPc-T:kHSDWbSVL2qCs2OBRU69NGysoiU=";
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
                upgradeService.upgrade("QQ",url,true,false);
            }
        });
        getView().findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upgradeService.cancel("QQ");
            }
        });
        upgradeService.setOnUpgradeListener("QQ.apk",new OnUpgradeListener(){

            @Override
            public void upgrade(boolean focue) {

            }

            @Override
            public void progress(int speed, int progress) {

            }

            @Override
            public void onFinish(String filePath) {

            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
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
