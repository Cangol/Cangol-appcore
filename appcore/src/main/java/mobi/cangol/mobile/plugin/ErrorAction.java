package mobi.cangol.mobile.plugin;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by jince on 2017/3/22.
 */

public class ErrorAction extends AbstractAction {
    private static final String DEFAULT_MESSAGE = "Something was really wrong. Ha ha!";
    private int mCode;
    private String mMessage;
    private boolean mAsync;
    public ErrorAction() {
        mCode = AbstractActionResult.CODE_ERROR;
        mMessage = DEFAULT_MESSAGE;
        mAsync = false;
    }

    public ErrorAction(boolean isAsync,int code, String message) {
        this.mCode = code;
        this.mMessage = message;
        this.mAsync = isAsync;
    }

    @Override
    public boolean isAsync(Context context, HashMap<String, String> requestData) {
        return mAsync;
    }

    @Override
    public AbstractActionResult invoke(Context context, HashMap<String, String> requestData) {
        AbstractActionResult result = new AbstractActionResult.Builder()
                .code(mCode)
                .msg(mMessage)
                .data(null)
                .object(null)
                .build();
        return result;
    }

}
