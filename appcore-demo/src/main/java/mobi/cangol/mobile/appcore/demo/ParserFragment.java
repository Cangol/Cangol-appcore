package mobi.cangol.mobile.appcore.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.parser.JSONParserException;
import mobi.cangol.mobile.parser.JsonUtils;
import mobi.cangol.mobile.utils.TimeUtils;

/**
 * Created by weixuewu on 16/4/30.
 */
public class ParserFragment extends Fragment {
    private static final String TAG="JsonUtils";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_database, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        test();
    }

    public void test(){

        Chat chat=new Chat();
        chat.setUid("100001");
        chat.setNickname("Cangol");
        chat.setContent("pleas test ");
        chat.setTime(TimeUtils.getCurrentTime());
        Log.d(TAG,"old chat="+chat);
        JSONObject json=JsonUtils.toJSONObject(chat);
        Log.d(TAG,"old chatJson="+json);
        Chat chatNew=null;
        try {
             chatNew=JsonUtils.parserToObject(Chat.class,json.toString());
        } catch (JSONParserException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"new chat="+chatNew);
    }
}
