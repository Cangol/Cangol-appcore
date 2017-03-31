package mobi.cangol.mobile.plugin;

import android.app.Application;
import android.content.Context;

import java.util.HashMap;

/**
 * Created by jince on 2017/3/22.
 */

public class PluginManager {
    private HashMap<String, AbstractPlugin> mPluginHashMap = null;
    public Application mContext;

    public PluginManager(Context context){
        mContext= (Application) context;
    }

    public void registerPlugin(String pluginName, AbstractPlugin plugin) {
        mPluginHashMap.put(pluginName, plugin);
    }


}
