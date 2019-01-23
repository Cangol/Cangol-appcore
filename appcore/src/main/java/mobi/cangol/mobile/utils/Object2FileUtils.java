/**
 * Copyright (c) 2013 Cangol
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.utils;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import mobi.cangol.mobile.logging.Log;

/**
 * @author Cangol
 */
public class Object2FileUtils {
    private static final String CHARSET = "UTF-8";

    private Object2FileUtils() {
    }

    /**
     * 写入json对象到文件
     *
     * @param jsonObject
     * @param objPath
     */
    public static void writeJSONObject2File(JSONObject jsonObject, String objPath) {
        try {
            writeJSONObject(jsonObject, new FileOutputStream(new File(objPath)));
        } catch (FileNotFoundException e) {
            Log.d(e.getMessage());
        }
    }

    /**
     * 写入json对象到文件
     *
     * @param jsonArray
     * @param objPath
     */
    public static void writeJSONArray2File(JSONArray jsonArray, String objPath) {
        try {
            writeJSONArray(jsonArray, new FileOutputStream(new File(objPath)));
        } catch (FileNotFoundException e) {
            Log.d(e.getMessage());
        }
    }

    /**
     * 读取文件到json对象
     *
     * @param jsonFile
     * @return
     */
    public static JSONArray readFile2JSONArray(File jsonFile) {
        JSONArray jsonArray = null;
        try {
            jsonArray = readJSONArray(new FileInputStream(jsonFile));
        } catch (FileNotFoundException e) {
            Log.d(e.getMessage());
        }
        return jsonArray;
    }

    /**
     * 读取文件到json对象
     *
     * @param jsonFile
     * @return
     */
    public static JSONObject readFile2JSONObject(File jsonFile) {
        JSONObject jsonObject = null;
        try {
            jsonObject = readJSONObject(new FileInputStream(jsonFile));
        } catch (FileNotFoundException e) {
            Log.d(e.getMessage());
        }
        return jsonObject;
    }

    /**
     * 写入json对象到输出流
     *
     * @param jsonArray
     * @param os
     */
    public static void writeJSONArray(JSONArray jsonArray, OutputStream os) {
        try {
            byte[] buffer = jsonArray.toString().getBytes(CHARSET);
            os.write(buffer);
            os.flush();
        } catch (IOException e) {
            Log.d(e.getMessage());
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Log.d(e.getMessage());
                }
            }
        }
    }

    /**
     * 写入json对象到输出流
     *
     * @param jsonObject
     * @param os
     */
    public static void writeJSONObject(JSONObject jsonObject, OutputStream os) {
        try {
            byte[] buffer = jsonObject.toString().getBytes(CHARSET);
            os.write(buffer);
            os.flush();
        } catch (Exception e) {
            Log.d(e.getMessage());
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Log.d(e.getMessage());
                }
            }
        }
    }

    /**
     * 从输入流读取json对象
     *
     * @param is
     * @return
     */
    public static JSONObject readJSONObject(InputStream is) {
        String content = null;
        JSONObject jsonObject = null;
        try {
            byte[] buffer = new byte[is.available()];
            if (is.read(buffer) != -1) {
                content = new String(buffer, CHARSET);
                if (!TextUtils.isEmpty(content)) {
                    jsonObject = new JSONObject(content);
                }

            }
        } catch (Exception e) {
            Log.d(e.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.d(e.getMessage());
                }
            }
        }
        return jsonObject;
    }

    /**
     * 从输入流读取json对象
     *
     * @param is
     * @return
     */
    public static JSONArray readJSONArray(InputStream is) {
        String content = null;
        JSONArray jsonArray = null;
        try {
            byte[] buffer = new byte[is.available()];
            if (is.read(buffer) != -1) {
                content = new String(buffer, CHARSET);
                if (!TextUtils.isEmpty(content)) {
                    jsonArray = new JSONArray(content);
                }

            }
        } catch (Exception e) {
            Log.d(e.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.d(e.getMessage());
                }
            }
        }
        return jsonArray;
    }

    /**
     * 将object对象写入输出流
     *
     * @param obj
     * @param out
     */
    public static void writeObject(Serializable obj, OutputStream out) {
        BufferedOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new BufferedOutputStream(out);
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
        } catch (Exception e) {
            Log.d(e.getMessage());
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (IOException e) {
                Log.d(e.getMessage());
            }

            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                Log.d(e.getMessage());
            }
        }
    }

    /**
     * 从输入流读取对象
     *
     * @param is
     * @return
     */
    public static Serializable readObject(InputStream is) {
        Object object = null;
        try {
            object = new ObjectInputStream(new BufferedInputStream(is)).readObject();
        } catch (Exception e) {
            Log.d(e.getMessage());
        }
        return (Serializable) object;
    }

    /**
     * 将对象写入文件
     *
     * @param obj
     * @param objPath
     */
    public static void writeObject(Serializable obj, String objPath) {

        File file = new File(objPath);
        if (file.exists()) {
            file.delete();
        }
        try {
            writeObject(obj, new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            Log.d(e.getMessage());
        }
    }

    /**
     * 从文件读取对象
     *
     * @param file
     * @return
     */
    public static Serializable readObject(File file) {
        if (!file.exists() || file.length() == 0) {
            return null;
        }
        Object object = null;
        try {
            object = readObject(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            Log.d(e.getMessage());
        }
        return (Serializable) object;
    }
}
