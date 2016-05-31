package mobi.cangol.mobile.appcore.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.parser.JSONParserException;
import mobi.cangol.mobile.parser.JsonUtils;
import mobi.cangol.mobile.parser.XMLParserException;
import mobi.cangol.mobile.parser.XmlUtils;

/**
 * Created by weixuewu on 16/4/30.
 */
public class ParserFragment extends Fragment {
    private static final String TAG="ParserFragment";
    private EditText editText1;
    private TextView textView1;
    private Button button1,button2,button3,button4,button5;
    private RadioGroup radioGroup;

    private String xmlStr="<?xml version='1.0' encoding='utf-8' standalone='yes' ?><ParserObject><name>Nick</name><id>1</id><height>1.75</height><isChild>true</isChild></ParserObject>";
    private String jsonStr=" {\"name\":\"Nick\",\"id\":1,\"height\":1.75,\"isChild\":true}";
    private boolean isJson=true;
    private ParserObject parserObject;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_parser, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }
    private void initViews() {
        editText1= (EditText) this.getView().findViewById(R.id.editText1);
        textView1 = (TextView) this.getView().findViewById(R.id.textView1);
        button1 = (Button) this.getView().findViewById(R.id.button1);
        button2 = (Button) this.getView().findViewById(R.id.button2);
        button3 = (Button) this.getView().findViewById(R.id.button3);
        button4 = (Button) this.getView().findViewById(R.id.button4);
        button5 = (Button) this.getView().findViewById(R.id.button5);
        radioGroup= (RadioGroup) this.getView().findViewById(R.id.radioGroup1);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioButton1:
                        editText1.setText(jsonStr);
                        isJson=true;
                        break;
                    case R.id.radioButton2:
                        editText1.setText(xmlStr);
                        isJson=false;
                        break;
                }
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str=editText1.getText().toString();
                if(!TextUtils.isEmpty(str)){
                    parser(str,isJson,true);
                 }else{
                    Toast.makeText(getActivity(),"解析内容不能为空!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str=editText1.getText().toString();
                if(parserObject!=null){
                    parser(str,isJson,true);
                }else{
                    Toast.makeText(getActivity(),"解析内容不能为空!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(parserObject!=null){
                    covertString(parserObject,isJson,false);
                }else{
                    Toast.makeText(getActivity(),"请先解析内容!",Toast.LENGTH_SHORT).show();
                }

            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(parserObject!=null){
                    covertString(parserObject,isJson,true);
                }else{
                    Toast.makeText(getActivity(),"请先解析内容!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView1.setText("--------------Converter---------------\n");
            }
        });

        textView1.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView1.setText("--------------Converter---------------\n");
        editText1.setText(jsonStr);
    }
    private void covertString(ParserObject parserObject,boolean json,boolean annotation) {
        Object object=null;
        try {
           object=json?JsonUtils.toJSONObject(parserObject,annotation):XmlUtils.toXml(parserObject,annotation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        printLog(object+"\n");
    }
    private void parser(String str, boolean json,boolean annotation) {
        try {
            parserObject=json?JsonUtils.parserToObject(ParserObject.class,str,annotation):
                    XmlUtils.parserToObject(ParserObject.class,str,annotation);
        } catch (JSONParserException e) {
            e.printStackTrace();
        } catch (XMLParserException e) {
            e.printStackTrace();
        }
        printLog(parserObject+"\n");
    }

    private void printLog(String message) {
        textView1.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView1.append(message);
        Log.d(message);
    }
}