/*
 *
 *  Copyright (c) 2013 Cangol
 *   <p/>
 *   Licensed under the Apache License, Version 2.0 (the "License")
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *  <p/>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p/>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package mobi.cangol.mobile.plugin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import dalvik.system.DexClassLoader;

/**
 * Created by xuewu.wei on 2016/4/14.
 */
public class PluginInfo {
    private Context context;
    private String apkPath;
    private boolean debug;
    private String name;
    private ClassLoader classLoader;
    private DexClassLoader dexClassLoader;

    public PluginInfo(String name, String apkPath) {
        this.name = name;
        this.apkPath = apkPath;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void launch() {

    }

    public void startActivity(String clazz, Bundle bundle) {
        Intent intent = null;
        try {
            intent = new Intent(context, Class.forName(clazz));
        } catch (ClassNotFoundException e) {
            Log.d("ClassNotFoundException", e.getMessage());
        }
        context.startActivity(intent);
    }

    public void startService(String clazz, Bundle bundle) {
        Intent intent = null;
        try {
            intent = new Intent(context, Class.forName(clazz));
        } catch (ClassNotFoundException e) {
            Log.d("ClassNotFoundException", e.getMessage());
        }
        context.startActivity(intent);
    }

    public String getApkPath() {
        return apkPath;
    }

    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public DexClassLoader getDexClassLoader() {
        return dexClassLoader;
    }

    public void setDexClassLoader(DexClassLoader dexClassLoader) {
        this.dexClassLoader = dexClassLoader;
    }

    public void onDestroy() {

    }
}
