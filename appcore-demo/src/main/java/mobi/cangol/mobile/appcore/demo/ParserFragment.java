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

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.Map;

import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.parser.Element;
import mobi.cangol.mobile.parser.JSONParserException;
import mobi.cangol.mobile.parser.JsonUtils;
import mobi.cangol.mobile.parser.XMLParserException;
import mobi.cangol.mobile.parser.XmlUtils;
import mobi.cangol.mobile.stat.StatAgent;

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
    private String jsonStr=" {\"name\":\"Nick\",\"id\":1,\"height\":1.75,\"isChild\":true,\"t\":true,\"r\":\"111\"}";
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
        editText1= this.getView().findViewById(R.id.editText1);
        textView1 = this.getView().findViewById(R.id.textView1);
        button1 = this.getView().findViewById(R.id.button1);
        button2 = this.getView().findViewById(R.id.button2);
        button3 = this.getView().findViewById(R.id.button3);
        button4 = this.getView().findViewById(R.id.button4);
        button5 = this.getView().findViewById(R.id.button5);
        radioGroup= this.getView().findViewById(R.id.radioGroup1);
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
                    parser(str,isJson,false);
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
            ParserObject<Boolean,String> test=new ParserObject<Boolean,String>();
            Class<ParserObject<Boolean,String>> clazz= (Class<ParserObject<Boolean,String>>) test.getClass();
            ParserObject tr = clazz.getDeclaredConstructor().newInstance();
            Log.e(""+clazz.cast(tr).getT());
            Log.e(""+clazz.cast(tr).getR());
            Map<String,Class> typeMap=new HashMap<String,Class>();

            for(TypeVariable type:clazz.getTypeParameters()){
                Log.e("type:"+type.getName());
            }
            for(Field field:clazz.getDeclaredFields()){
                field.setAccessible(true);
                Log.e("field:"+field.getName()+","+field.getGenericType().getClass());
            }

            parserObject=json?JsonUtils.parserToObject(clazz,str,annotation):
                    XmlUtils.parserToObject(clazz,str,annotation);

        }catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }
        printLog(parserObject+"\n");
    }
    public static Class getSuperClassGenricType(Class clazz, int index) throws IndexOutOfBoundsException {

        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class) params[index];
    }
    private void printLog(String message) {
        textView1.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView1.append(message);
        Log.d(message);
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
class ParserObject<T,R> {
    @Element("t")
    private T t;
    @Element("r")
    private R r;

    @Element("id")
    private int id;
    @Element("name")
    private String name;
    @Element("defStr")
    private String defStr="defStr";
    @Element("_HEIGHT")
    private double height;
    @Element("_IS_CHILD")
    private boolean isChild;
    public ParserObject() {

    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public R getR() {
        return r;
    }

    public void setR(R r) {
        this.r = r;
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

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public boolean isChild() {
        return isChild;
    }

    public void setChild(boolean child) {
        isChild = child;
    }

    public String getDefStr() {
        return defStr;
    }

    public void setDefStr(String defStr) {
        this.defStr = defStr;
    }

    @Override
    public String toString() {
        return "ParserObject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", height=" + height +
                ", isChild=" + isChild +
                ", defStr=" + defStr +
                ", t=" + t +
                ", r=" + r +
                '}';
    }
}