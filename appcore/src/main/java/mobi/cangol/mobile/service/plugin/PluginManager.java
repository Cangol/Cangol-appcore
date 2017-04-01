package mobi.cangol.mobile.service.plugin;

import mobi.cangol.mobile.service.AppService;

/**
 * Created by jince on 2017/3/22.
 */

public interface PluginManager  extends AppService{
     void registerPlugin(String pluginName, AbstractPlugin plugin);
}
