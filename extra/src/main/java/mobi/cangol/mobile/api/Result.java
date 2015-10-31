package mobi.cangol.mobile.api;

import org.json.JSONObject;

import java.util.List;

public abstract class Result<T>  {


    protected boolean success;
    protected String source;
    protected String message;
    protected String code;
    protected T object;
    protected List<T> list;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    /**
     * 返回一个解析好的Result对象
     * @param c
     * @param json
     * @param root
     * @param <T>
     * @return
     */
    public abstract <T> Result<T> parserResult(Class<T> c, JSONObject json, String root);
}
