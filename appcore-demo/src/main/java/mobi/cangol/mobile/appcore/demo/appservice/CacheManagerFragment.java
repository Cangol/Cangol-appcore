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

import java.io.Serializable;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.appcore.demo.R;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.cache.CacheLoader;
import mobi.cangol.mobile.service.cache.CacheManager;
import mobi.cangol.mobile.service.cache.CacheObject;

/**
 * Created by weixuewu on 16/4/30.
 */
public class CacheManagerFragment extends Fragment{
    private static String  TAG="CacheManagerFragment";
    private CacheManager cacheManager;
    private TextView textView1;
    private Button button1, button2,button3,button4,button5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cacheManager = (CacheManager) ((CoreApplication) this.getActivity().getApplicationContext()).getAppService(AppService.CACHE_MANAGER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_manager_cache, container, false);
        return v;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }
    private void initViews(){
        textView1 = (TextView) this.getView().findViewById(R.id.textView1);
        button1 = (Button) this.getView().findViewById(R.id.button1);
        button2 = (Button) this.getView().findViewById(R.id.button2);
        button3 = (Button) this.getView().findViewById(R.id.button3);
        button4 = (Button) this.getView().findViewById(R.id.button4);
        button5 = (Button) this.getView().findViewById(R.id.button5);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user=new User(1,"Jick","12");
                cacheManager.addContent(TAG,"user",user, CacheObject.TIME_MIN*1);
                updateViews();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user=new User(2,"Rose.Any","24");
                cacheManager.addContent(TAG,"user",user);
                updateViews();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cacheManager.removeContent(TAG,"user");
                updateViews();
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cacheManager.clearCache();
                updateViews();
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cacheManager.getContent(TAG, "user", new CacheLoader() {
                    @Override
                    public void loading() {
                        textView1.setText("--------------cache---------------");
                        textView1.append("\n loading");
                        textView1.append("\nsize=" + cacheManager.size());
                        textView1.append("\nuser=loading");
                    }

                    @Override
                    public void returnContent(Object content) {
                        textView1.setText("--------------cache---------------");
                        textView1.append("\nsize=" + cacheManager.size());
                        textView1.append("\nuser=" + cacheManager.getContent(TAG,"user"));
                    }
                });
            }
        });
    }
    private void updateViews() {
        textView1.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView1.setText("--------------cache---------------");
        textView1.append("\nsize=" + cacheManager.size());
        textView1.append("\nuser=" + cacheManager.getContent(TAG,"user"));

        Log.d(textView1.getText().toString());
    }
}
class User implements Serializable{
    private String name;
    private String age;
    private int id;
    public User(){}

    public User(int id,String name,  String age) {
        this.age = age;
        this.id = id;
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id  +
                ", name='" + name + '\'' +
                ", age='" + age + '\'' +
                '}';
    }
}