package mobi.cangol.mobile.appcore.demo;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.session.SessionService;
import mobi.cangol.mobile.service.upgrade.UpgradeListener;
import mobi.cangol.mobile.service.upgrade.UpgradeService;
import mobi.cangol.mobile.utils.TimeUtils;

public class MainActivity extends Activity {
    private String url="http://180.153.105.145/dd.myapp.com/16891/8E5A9885970F76080F8445C652DE347C.apk?mkey=5715c34fc20a8141&f=d511&fsname=com.tencent.mobileqq_6.3.1_350.apk&p=.apk";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        SessionService session=((CoreApplication) this.getApplicationContext()).getSession();
        if(session.containsKey("key")){
            Log.d(">>",session.getString("key","ddd"));
        }else{
            session.saveString("key", "value=" + TimeUtils.getCurrentHoursMinutes());
        }

        this.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upgrade();
            }
        });

    }
    private void upgrade(){
        UpgradeService upgradeService= (UpgradeService) ((CoreApplication) this.getApplicationContext()).getAppService(AppService.UPGRADE_SERVICE);
        upgradeService.upgradeApk("QQ",url,false);
        upgradeService.setOnUpgradeListener(new UpgradeListener(){

            @Override
            public void upgrade(boolean constraint) {

            }

            @Override
            public void onFinish() {

            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("app.exit");
        ((CoreApplication)this.getApplicationContext()).exit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //StatAgent.getInstance(this).onActivityResume("MainActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //StatAgent.getInstance(this).onActivityPause("MainActivity");
    }
}
