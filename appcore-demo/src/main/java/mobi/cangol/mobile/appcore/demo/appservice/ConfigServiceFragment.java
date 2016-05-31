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

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.appcore.demo.R;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.conf.ConfigService;

/**
 * Created by weixuewu on 16/4/30.
 */
public class ConfigServiceFragment extends Fragment {
    private ConfigService configService;
    private TextView textView1;
    private Button button1, button2,button3,button4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configService = (ConfigService) ((CoreApplication) this.getActivity().getApplicationContext()).getAppService(AppService.CONFIG_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_service_config, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        updateViews();
    }
    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
    private void initViews() {
        textView1 = (TextView) this.getView().findViewById(R.id.textView1);
        button1 = (Button) this.getView().findViewById(R.id.button1);
        button2 = (Button) this.getView().findViewById(R.id.button2);
        button3 = (Button) this.getView().findViewById(R.id.button3);
        button4 = (Button) this.getView().findViewById(R.id.button4);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path="/sdcard/appcore";
                boolean result=configService.setCustomAppDir(path);
                showToast("setCustomAppDir "+(result?"success":"fail")+"\n"+path);
                updateViews();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configService.resetAppDir();
                updateViews();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configService.setUseInternalStorage(true);
                updateViews();
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configService.setUseInternalStorage(false);
                updateViews();
            }
        });
    }

    private void updateViews() {
        textView1.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView1.setText("--------------config---------------");
        textView1.append("\nisCustomAppDir=" + configService.isCustomAppDir());
        textView1.append("\nisUseInternalStorage=" + configService.isUseInternalStorage());
        textView1.append("\n\ngetAppDir=" + configService.getAppDir());
        textView1.append("\n\ngetCacheDir=" + configService.getCacheDir());
        textView1.append("\n\ngetDownloadDir=" + configService.getDownloadDir());
        textView1.append("\n\ngetUpgradeDir=" + configService.getUpgradeDir());
        textView1.append("\n\ngetTempDir=" + configService.getTempDir());
        textView1.append("\n\ngetImageDir=" + configService.getImageDir());

        Log.d(textView1.getText().toString());
    }
}
