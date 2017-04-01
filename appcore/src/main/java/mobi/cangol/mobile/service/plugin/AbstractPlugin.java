package mobi.cangol.mobile.service.plugin;

import java.util.HashMap;

/**
 * Created by jince on 2017/3/22.
 */

public abstract class AbstractPlugin {
    private boolean mValid = true;
    private HashMap<String, AbstractAction> mActions;

    public AbstractPlugin() {
        mActions = new HashMap<>();
        registerActions();
    }

    protected void registerAction(String actionName, AbstractAction action) {
        mActions.put(actionName, action);
    }

    public AbstractAction findAction(String actionName) {
        return mActions.get(actionName);
    }

    public boolean isValid() {
        return mValid;
    }

    protected abstract void registerActions();
}
