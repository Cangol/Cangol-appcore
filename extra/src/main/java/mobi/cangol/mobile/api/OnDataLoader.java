package mobi.cangol.mobile.api;


abstract public class OnDataLoader<T> {
    private Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    /**
     * 开始
     */
    abstract public void onStart();

    /**
     * @param t
     */
    abstract public void onSuccess(T t);

    /**
     * 失败
     *
     * @param code
     * @param message
     */
    abstract public void onFailure(String code, String message);

}
