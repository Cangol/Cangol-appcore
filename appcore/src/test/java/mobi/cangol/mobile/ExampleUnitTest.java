package mobi.cangol.mobile;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.parser.Element;
import mobi.cangol.mobile.parser.JSONParserException;
import mobi.cangol.mobile.parser.JsonUtils;
import mobi.cangol.mobile.parser.XMLParserException;
import mobi.cangol.mobile.parser.XmlUtils;

import static junit.framework.Assert.assertEquals;


/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void test1() throws Exception {
        String jsonStr=" {\"name\":\"Nick\",\"id\":1,\"height\":1.75,\"isChild\":true,\"t\":true,\"r\":\"111\"}";
        parser(jsonStr);
    }
    private void parser(String str) {
        try {
            ParserObject<Boolean,String> test=new ParserObject<Boolean,String>();
            Class<ParserObject<Boolean,String>> clazz= (Class<ParserObject<Boolean,String>>) test.getClass();
            ParserObject tr = clazz.getDeclaredConstructor().newInstance();
            Map<String,Class> typeMap=new HashMap<String,Class>();

            for(TypeVariable type:clazz.getTypeParameters()){
                System.out.println("type:"+type.getName()+","+type.getGenericDeclaration());
            }
//            for(Method method:clazz.getDeclaredMethods()){
//                System.out.println("method:"+method.getName()+","+method.getReturnType());
//            }

            ParserObject<Boolean,String> parserObject=JsonUtils.parserToObject(clazz,str,true);
            System.out.println(parserObject.toString());

        } catch (JSONParserException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
class ParserObject<T,R> {
//    @Element("t")
//    private T t;
//    @Element("r")
//    private R r;

    @Element("id")
    private int id;
    @Element("name")
    private String name;
    @Element("_HEIGHT")
    private double height;
    @Element("_IS_CHILD")
    private boolean isChild;
    public ParserObject() {

    }

//    public T getT() {
//        return t;
//    }
//
//    public void setT(T t) {
//        this.t = t;
//    }
//
//    public R getR() {
//        return r;
//    }
//
//    public void setR(R r) {
//        this.r = r;
//    }

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

    @Override
    public String toString() {
        return "ParserObject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", height=" + height +
                ", isChild=" + isChild +
//                ", t=" + t +
//                ", r=" + r +
                '}';
    }
}



