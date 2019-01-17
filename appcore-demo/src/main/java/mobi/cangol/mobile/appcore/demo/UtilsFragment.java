package mobi.cangol.mobile.appcore.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.utils.DeviceInfo;

/**
 * Created by weixuewu on 16/4/30.
 */
public class UtilsFragment extends Fragment {
    private static final String TAG = "UtilsFragment";
    private TextView textView1;
    private Button button1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_utils, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        textView1 = this.getView().findViewById(R.id.textView1);
        button1 = this.getView().findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView1.setMovementMethod(ScrollingMovementMethod.getInstance());
                textView1.setText("\n--------------Utils---------------\n");
                StringBuffer sb=new StringBuffer();

                sb.append("\nCharset="+DeviceInfo.getCharset());
                sb.append("\nCountry="+DeviceInfo.getCountry());
                sb.append("\nLanguage="+DeviceInfo.getLanguage());
                sb.append("\nLocale="+DeviceInfo.getLocale());
                sb.append("\nOS="+DeviceInfo.getOS());
                sb.append("\nOSVersion="+DeviceInfo.getOSVersion());
                sb.append("\nDeviceBrand="+DeviceInfo.getDeviceBrand());
                sb.append("\nAppVersion="+DeviceInfo.getAppVersion(getContext()));
                sb.append("\nSHA1Fingerprint="+DeviceInfo.getSHA1Fingerprint(getContext()));
                sb.append("\nMD5Fingerprint="+DeviceInfo.getMD5Fingerprint(getContext()));
                sb.append("\nOpenUDID="+DeviceInfo.getOpenUDID(getContext()));
                sb.append("\ngetMobileInfo="+DeviceInfo.getMobileInfo()+"\n---------|\n");
                sb.append("\nMemTotalSize="+DeviceInfo.getMemTotalSize());
                sb.append("\nMemFreeSize="+DeviceInfo.getMemFreeSize());
                sb.append("\nMemUsedPer="+(100f*(DeviceInfo.getMemTotalSize()-DeviceInfo.getMemFreeSize())/DeviceInfo.getMemTotalSize()*1.0f)+"%");

                sb.append("\nMemInfo="+DeviceInfo.getMemInfo()+"\n---------|\n");
                sb.append("\nCPUABI="+DeviceInfo.getCPUABI());
                sb.append("\nCPUInfo="+DeviceInfo.getCPUInfo()+"\n---------|\n");
                sb.append("\nResolution="+DeviceInfo.getResolution(getContext()));
                sb.append("\nDensity="+DeviceInfo.getDensity(getContext()));
                sb.append("\nDensityDpi="+DeviceInfo.getDensityDpi(getContext()));
                sb.append("\nDensityDpiStr="+DeviceInfo.getDensityDpiStr(getContext()));
                sb.append("\nScreenSize="+DeviceInfo.getScreenSize(getContext()));
                sb.append("\nStatusBarHeight="+DeviceInfo.getStatusBarHeight(getContext()));
                sb.append("\nNavigationBarHeight="+DeviceInfo.getNavigationBarHeight(getContext()));
                sb.append("\nDisplayMetrics="+DeviceInfo.getDisplayMetrics(getContext()));

                sb.append("\nisConnection="+DeviceInfo.isConnection(getContext()));
                sb.append("\nisWifiConnection="+DeviceInfo.isWifiConnection(getContext()));
                sb.append("\ngetIpStr="+DeviceInfo.getIpStr(getContext()));
                sb.append("\ngetIpAddress="+DeviceInfo.getIpAddress(getContext()));
                sb.append("\nMacAddress="+DeviceInfo.getMacAddress(getContext()));

                sb.append("\nNetworkOperatorName="+DeviceInfo.getNetworkOperatorName(getContext()));
                sb.append("\nNetworkTypeName="+DeviceInfo.getNetworkTypeName(getContext()));
                sb.append("\nNetworkClassName="+DeviceInfo.getNetworkClassName(getContext()));
                sb.append("\nWifiRssiString="+DeviceInfo.getWifiRssiString(getContext()));
                sb.append("\nWifiRssi="+DeviceInfo.getWifiRssi(getContext()));

                sb.append("\nisAppProcess="+DeviceInfo.isAppProcess(getContext()));
                sb.append("\nisProxy="+DeviceInfo.isProxy(getContext()));

                printLog(sb.toString());
            }
        });
    }
    private void printLog(String message) {
        textView1.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView1.append(message);
        Log.d(message);
    }
}
