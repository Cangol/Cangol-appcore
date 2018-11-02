package mobi.cangol.mobile.appcore.libdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import mobi.cangol.mobile.service.route.Route;

/**
 * Created by weixuewu on 16/4/30.
 */
//@Route(path = "lib",standalone = true)
public class LibTestFragment extends Fragment {
    private static final String TAG = "LibTestFragment";
    private TextView textView1;
    private Button button1;
    private String key;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        key=getArguments().getString("key");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_test, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        textView1 = (TextView) this.getView().findViewById(R.id.textView1);
        button1 = (Button) this.getView().findViewById(R.id.button1);
        button1.setText(""+key);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}
