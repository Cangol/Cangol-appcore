package mobi.cangol.mobile.appcore.demo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import mobi.cangol.mobile.service.route.Route;

/**
 * Created by weixuewu on 16/4/30.
 */
@Route(path = "app/test")
public class TestFragment extends Fragment {
    private static final String TAG = "AppTestFragment";
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
        View v = inflater.inflate(mobi.cangol.mobile.appcore.libdemo.R.layout.fragment_test, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        getActivity().setTitle(TestFragment.class.getSimpleName().replace("Fragment",""));
        textView1 = this.getView().findViewById(mobi.cangol.mobile.appcore.libdemo.R.id.textView1);
        button1 = this.getView().findViewById(mobi.cangol.mobile.appcore.libdemo.R.id.button1);
        button1.setText(""+key);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}
