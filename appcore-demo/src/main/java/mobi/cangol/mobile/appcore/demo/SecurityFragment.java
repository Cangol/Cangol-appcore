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
import android.widget.TextView;

import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;

import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.security.AESUtils;
import mobi.cangol.mobile.security.Base64;
import mobi.cangol.mobile.security.RSAUtils;

/**
 * Created by weixuewu on 16/4/30.
 */
public class SecurityFragment extends Fragment {
    private EditText editText1,editText2;
    private TextView textView1,textView2;
    private Button button1,button2,button3,button4,button5,button6,button7,button8;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_security, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        editText1= this.getView().findViewById(R.id.editText1);
        editText2= this.getView().findViewById(R.id.editText2);
        textView1 = this.getView().findViewById(R.id.textView1);

        button1 = this.getView().findViewById(R.id.button1);
        button2 = this.getView().findViewById(R.id.button2);
        button3 = this.getView().findViewById(R.id.button3);
        button4 = this.getView().findViewById(R.id.button4);
        button5 = this.getView().findViewById(R.id.button5);
        button6 = this.getView().findViewById(R.id.button6);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str=editText1.getText().toString();
                if(!TextUtils.isEmpty(str))
                    aesEncode(str);

            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str=editText2.getText().toString();
                if(!TextUtils.isEmpty(str))
                    aesDecode(str);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str=editText1.getText().toString();
                if(!TextUtils.isEmpty(str))
                    base64Encode(str);

            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str=editText2.getText().toString();
                if(!TextUtils.isEmpty(str))
                    base64Decode(str);
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str=editText1.getText().toString();
                if(!TextUtils.isEmpty(str))
                    rsaEncode(str);

            }
        });
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str=editText2.getText().toString();
                if(!TextUtils.isEmpty(str))
                    rsaDecode(str);
            }
        });

        textView1.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView1.setText("--------------Security---------------");
    }
    private void printLog(String message) {
        textView1.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView1.setText("--------------Security---------------");
        textView1.append("\n"+message);
        Log.d(message);
    }
    private void aesEncode(String str) {
        String content=""+str;
        try {
            String result=AESUtils.encrypt("12345678",content);
            editText2.setText(result);
            printLog(content+" -->> "+result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void aesDecode(String str) {
        String content=""+str;
        try {
            String result=AESUtils.decrypt("12345678",content);
            editText1.setText(result);
            printLog(content+" -->> "+result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void base64Encode(String str) {
        String content=""+str;
        try {
            String result=Base64.encode(content);
            editText2.setText(result);
            printLog(content+" -->> "+result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void base64Decode(String str) {
        String content=""+str;
        try {
            String result=Base64.decode(content);
            editText1.setText(result);
            printLog(content+" -->> "+result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static{
        HashMap<String, Object> map = null;
        try {
            map = RSAUtils.getKeys();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //生成公钥和私钥
        RSAPublicKey publicKey = (RSAPublicKey) map.get("public");
        RSAPrivateKey privateKey = (RSAPrivateKey) map.get("private");

        //模
        String modulus = publicKey.getModulus().toString();
        //公钥指数
        String public_exponent = publicKey.getPublicExponent().toString();
        //私钥指数
        String private_exponent = privateKey.getPrivateExponent().toString();
        //明文
        String ming = "123456789";
        //使用模和指数生成公钥和私钥
         pubKey = RSAUtils.getPublicKey(modulus, public_exponent);
         priKey = RSAUtils.getPrivateKey(modulus, private_exponent);
    }
    static RSAPublicKey pubKey;
    static RSAPrivateKey priKey;
    private void rsaEncode(String str) {
        String content=""+str;
        try {
            String result=RSAUtils.encryptByPublicKey(content,pubKey);
            editText2.setText(result);
            printLog(content+" -->> "+result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void rsaDecode(String str) {
        String content=""+str;
        try {
            String result=RSAUtils.decryptByPrivateKey(content,priKey);
            editText1.setText(result);
            printLog(content+" -->> "+result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}
