package mobi.cangol.mobile.api;

import android.content.Context;

import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Map;

import mobi.cangol.mobile.logging.Log;

/**
 * 处理API网络请求
 */
public class ApiRequest {
    public static final boolean DEBUG = true;
    private Context mContext;
    private String mMethod;
    private String mUrl;
    private HashMap<String, Object> mParams;
    private String mTag;
    private boolean mRunning = false;
    private ApiClient mApiClient;
    private String mRoot;
    private Class<? extends Result> resultClass = Result.class;

    private ApiRequest(Context context, String tag) {
        mContext = context;
        mParams = new HashMap<String, Object>();
        mTag = tag;
        mMethod = "GET";
        mApiClient = ApiClient.getInstance(mContext);
    }

    /**
     * 设置结果处理类
     *
     * @param clazz
     */
    public void setResultClass(Class<? extends Result> clazz) {
        this.resultClass = clazz;
    }

    /**
     * 产生一个ApiRequest实例
     *
     * @param context
     * @param tag
     * @return
     */
    public static ApiRequest build(Context context, String tag) {
        return new ApiRequest(context, tag);
    }

    /**
     * 判断是否在运行
     *
     * @return
     */
    public boolean IsRunning() {
        return mRunning;
    }

    /**
     * 取消请求
     *
     * @param mayInterruptIfRunning
     */
    public void cancel(boolean mayInterruptIfRunning) {
        mApiClient.cancel(mTag, mayInterruptIfRunning);
    }

    /**
     * 取消请求
     *
     * @param context
     * @param tag
     */
    public static void cancel(Context context, String tag) {
        ApiClient.getInstance(context).cancel(tag, true);
    }

    /**
     * 设置url
     *
     * @param url
     */
    public void setUrl(String url) {
        this.mUrl = url;
    }

    /**
     * 设置http请求方式
     *
     * @param method
     */
    public void setMethod(String method) {
        this.mMethod = method;
    }

    /**
     * 设置String型参数集
     *
     * @param params
     */
    public void setParams(HashMap<String, String> params) {
        for (Map.Entry<String, String> entry : params.entrySet()) {
            mParams.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 设置Object型参数集
     *
     * @param params
     */
    public void setObjectParams(HashMap<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            mParams.put(entry.getKey(), entry.getValue());
        }
    }

    private String getAction(String url) {
        String action = url.substring(url.indexOf("/", 8));
        action = action.substring(0, action.contains("?") ? action.indexOf("?") : action.length());
        return action;
    }

    /**
     * 设置解析根节点
     *
     * @param root
     */
    public void setRoot(String root) {
        this.mRoot = root;
    }

    /**
     * 返回参数集
     *
     * @return
     */
    public HashMap<String, Object> getParams() {
        return mParams;
    }

    /**
     * 深度递归获取泛型
     **/
    private Class<?> getGenericClass(Type type) {
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getActualTypeArguments()[0];
            return getGenericClass(type);
        } else {
            return (Class<?>) type;
        }
    }

    /**
     * 解析Result
     **/
    private <T> Result<T> parserResult(Class<? extends Result> clazz, Class<T> c, Object response, String root) {
        if (clazz == null || clazz == Result.class) {
            throw new IllegalArgumentException("must be setResultClass");
        } else {
            Result result = null;
            try {
                result = clazz.newInstance();
                return result.parserResult(c, response, root);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    /**
     * 执行请求
     *
     * @param onDataLoader
     * @param <T>
     */

    public <T> void execute(final OnDataLoader<T> onDataLoader) {
        this.execute(mMethod, mUrl, mParams, mRoot, onDataLoader);
    }

    /**
     * 执行请求 返回json结果
     *
     * @param methodStr
     * @param url
     * @param params
     * @param root
     * @param onDataLoader
     * @param <T>
     */
    public <T> void execute(String methodStr, String url, HashMap<String, Object> params, String root, final OnDataLoader<T> onDataLoader) throws IllegalFormatException {
        this.mMethod = methodStr;
        this.mUrl = url;
        this.mParams = params;
        this.mRoot = root;
        ApiClient.Method method;
        if ("GET".equals(methodStr.toUpperCase())) {
            method = ApiClient.Method.GET;
        } else {
            method = ApiClient.Method.POST;
        }
        if(resultClass==Result.class){
            throw new IllegalArgumentException("you must set json resultClass ");
        }
        mApiClient.execute(mTag, method, mUrl, mParams, mRoot, new ApiClient.OnJsonResponse() {
            @Override
            public void onStart() {
                if (onDataLoader != null) onDataLoader.onStart();
            }

            @Override
            public void onSuccess(JSONObject response) {
                if (DEBUG) Log.d("onSuccess response=" + response);

                if (DEBUG) Log.d("Converter resultClass:" + resultClass);

                Class<?> c = getGenericClass(onDataLoader.getClass().getGenericSuperclass());
                if (DEBUG) Log.d("Converter class:" + c);

                Result result = parserResult(resultClass, c, response, mRoot);

                if (onDataLoader != null) onDataLoader.setResult(result);

                if (result.isSuccess()) {
                    if (result.getObject() != null) {
                        if (onDataLoader != null) onDataLoader.onSuccess((T) result.getObject());
                    } else {
                        if (onDataLoader != null) onDataLoader.onSuccess((T) result.getList());
                    }
                } else {
                    if (onDataLoader != null)
                        onDataLoader.onFailure(result.getCode(), result.getMessage());
                }
            }

            @Override
            public void onFailure(String code, String response) {
                if (DEBUG) Log.d("onFailure code=" + code + ",response=" + response);
                if (onDataLoader != null) onDataLoader.onFailure(code, response);

            }
        });
    }

    /**
     * 执行请求 返回String结果
     *
     * @param methodStr
     * @param url
     * @param params
     * @param onDataLoader
     * @param <T>
     */

    public <T> void executeResultString(String methodStr, String url, HashMap<String, Object> params, final OnDataLoader<T> onDataLoader) {
        this.mMethod = methodStr;
        this.mUrl = url;
        this.mParams = params;
        this.mRoot = null;
        ApiClient.Method method;
        if ("GET".equals(methodStr.toUpperCase())) {
            method = ApiClient.Method.GET;
        } else {
            method = ApiClient.Method.POST;
        }
        if(resultClass==Result.class){
            throw new IllegalArgumentException("you must set String resultClass ");
        }
        mApiClient.execute(mTag, method, mUrl, mParams, mRoot, new ApiClient.OnStringResponse() {
            @Override
            public void onStart() {
                if (onDataLoader != null) onDataLoader.onStart();
            }

            @Override
            public void onSuccess(String response) {
                if (DEBUG) Log.d("onSuccess response=" + response);

                if (DEBUG) Log.d("Converter resultClass:" + resultClass);

                Class<?> c = getGenericClass(onDataLoader.getClass().getGenericSuperclass());
                if (DEBUG) Log.d("Converter class:" + c);

                Result result = parserResult(resultClass, c, response, mRoot);

                if (onDataLoader != null) onDataLoader.setResult(result);

                if (result.isSuccess()) {
                    if (result.getObject() != null) {
                        if (onDataLoader != null) onDataLoader.onSuccess((T) result.getObject());
                    } else {
                        if (onDataLoader != null) onDataLoader.onSuccess((T) result.getList());
                    }
                } else {
                    if (onDataLoader != null)
                        onDataLoader.onFailure(result.getCode(), result.getMessage());
                }
            }

            @Override
            public void onFailure(String code, String response) {
                if (DEBUG) Log.d("onFailure code=" + code + ",response=" + response);
                if (onDataLoader != null) onDataLoader.onFailure(code, response);

            }
        });
    }

    /**
     * 执行请求 返回byte[]结果
     *
     * @param methodStr
     * @param url
     * @param params
     * @param onDataLoader
     * @param <T>
     */
    public <T> void executeResultBinary(String methodStr, String url, HashMap<String, Object> params, final OnDataLoader<T> onDataLoader) {
        this.mMethod = methodStr;
        this.mUrl = url;
        this.mParams = params;
        this.mRoot = null;
        ApiClient.Method method;
        if ("GET".equals(methodStr.toUpperCase())) {
            method = ApiClient.Method.GET;
        } else {
            method = ApiClient.Method.POST;
        }
        if(resultClass==Result.class){
            throw new IllegalArgumentException("you must set Binary resultClass ");
        }
        mApiClient.execute(mTag, method, mUrl, mParams, mRoot, new ApiClient.OnBinaryResponse() {
            @Override
            public void onStart() {
                if (onDataLoader != null) onDataLoader.onStart();
            }

            @Override
            public void onSuccess(byte[] response) {
                if (DEBUG) Log.d("onSuccess response=" + response);

                if (DEBUG) Log.d("Converter resultClass:" + resultClass);

                Class<?> c = getGenericClass(onDataLoader.getClass().getGenericSuperclass());

                Result result = parserResult(resultClass, c, response, mRoot);

                if (onDataLoader != null) onDataLoader.setResult(result);

                if (result.isSuccess()) {
                    if (result.getObject() != null) {
                        if (onDataLoader != null) onDataLoader.onSuccess((T) result.getObject());
                    } else {
                        if (onDataLoader != null) onDataLoader.onSuccess((T) result.getList());
                    }
                } else {
                    if (onDataLoader != null)
                        onDataLoader.onFailure(result.getCode(), result.getMessage());
                }
            }

            @Override
            public void onFailure(String code, String response) {
                if (DEBUG) Log.d("onFailure code=" + code + ",response=" + response);
                if (onDataLoader != null) onDataLoader.onFailure(code, response);

            }
        });
    }
}
