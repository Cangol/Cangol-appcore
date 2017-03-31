package mobi.cangol.mobile.plugin;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by jince on 2017/3/22.
 */

public abstract class AbstractAction {
    public abstract boolean isAsync(Context context, HashMap<String, String> requestData);

    public abstract AbstractActionResult invoke(Context context, HashMap<String, String> requestData);

    public boolean isAsync(Context context, HashMap<String, String> requestData, Object object) {
        return false;
    }

    public AbstractActionResult invoke(Context context, HashMap<String, String> requestData, Object object) {
        return new AbstractActionResult.Builder().code(AbstractActionResult.CODE_NOT_IMPLEMENT).msg("This method has not yet been implemented.").build();
    }
}
