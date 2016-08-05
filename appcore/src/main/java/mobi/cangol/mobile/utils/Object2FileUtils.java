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
import org.json.JSONException;
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
import java.io.StreamCorruptedException;

import mobi.cangol.mobile.logging.Log;

/**
 * @author Cangol
 */
public class Object2FileUtils {
    private final static String CHARSET = "UTF-8";

    /**
     * 写入json对象到文件
     * @param jsonObject
     * @param objPath
     */
    public static void writeJSONObject2File(JSONObject jsonObject, String objPath) {
        File file = new File(objPath);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            writeJSONObject(jsonObject, fileOutputStream);
        } catch (FileNotFoundException e) {
            Log.d(e.getMessage());
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    Log.d(e.getMessage());
                }
                fileOutputStream = null;
            }
        }
    }

    /**
     * 写入json对象到文件
     * @param jsonArray
     * @param objPath
     */
    public static void writeJSONArray2File(JSONArray jsonArray, String objPath) {
        File file = new File(objPath);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            writeJSONArray(jsonArray, fileOutputStream);
        } catch (FileNotFoundException e) {
            Log.d(e.getMessage());
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    Log.d(e.getMessage());
                }
                fileOutputStream = null;
            }
        }
    }

    /**
     * 读取文件到json对象
     * @param jsonFile
     * @return
     */
    public static JSONArray readFile2JSONArray(File jsonFile) {
        FileInputStream fileInputStream = null;
        JSONArray jsonArray = null;
        try {
            fileInputStream = new FileInputStream(jsonFile);
            jsonArray = readJSONArray(fileInputStream);
        } catch (FileNotFoundException e) {
            Log.d(e.getMessage());
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    Log.d(e.getMessage());
                }
                fileInputStream = null;
            }
        }
        return jsonArray;
    }

    /**
     * 读取文件到json对象
     * @param jsonFile
     * @return
     */
    public static JSONObject readFile2JSONObject(File jsonFile) {
        FileInputStream fileInputStream = null;
        JSONObject jsonObject = null;
        try {
            fileInputStream = new FileInputStream(jsonFile);
            jsonObject = readJSONObject(fileInputStream);
        } catch (FileNotFoundException e) {
            Log.d(e.getMessage());
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    Log.d(e.getMessage());
                }
                fileInputStream = null;
            }
        }
        return jsonObject;
    }

    /**
     * 写入json对象到输出流
     * @param jsonArray
     * @param os
     */
    public static void writeJSONArray(JSONArray jsonArray, OutputStream os) {
        try {
            byte[] buffer = jsonArray.toString().getBytes(CHARSET);
            os.write(buffer);
            os.flush();
        } catch (FileNotFoundException e) {
            Log.d(e.getMessage());
        } catch (IOException e) {
            Log.d(e.getMessage());
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Log.d(e.getMessage());
                }
                os = null;
            }
        }
    }

    /**
     * 写入json对象到输出流
     * @param jsonObject
     * @param os
     */
    public static void writeJSONObject(JSONObject jsonObject, OutputStream os) {
        try {
            byte[] buffer = jsonObject.toString().getBytes(CHARSET);
            os.write(buffer);
            os.flush();
        } catch (FileNotFoundException e) {
            Log.d(e.getMessage());
        } catch (IOException e) {
            Log.d(e.getMessage());
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Log.d(e.getMessage());
                }
                os = null;
            }
        }
    }

    /**
     * 从输入流读取json对象
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
        } catch (IOException e) {
            Log.d(e.getMessage());
        } catch (JSONException e) {
            Log.d(e.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.d(e.getMessage());
                }
                is = null;
            }
        }
        return jsonObject;
    }

    /**
     * 从输入流读取json对象
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
        } catch (IOException e) {
            Log.d(e.getMessage());
        } catch (JSONException e) {
            Log.d(e.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.d(e.getMessage());
                }
                is = null;
            }
        }
        return jsonArray;
    }

    /**
     * 将object对象写入输出流
     * @param obj
     * @param out
     */
    public static void writeObject(Serializable obj, OutputStream out) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new BufferedOutputStream(out));
            oos.writeObject(obj);
        } catch (FileNotFoundException e) {
            Log.d(e.getMessage());
        } catch (IOException e) {
            Log.d(e.getMessage());
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (IOException e) {
                Log.d(e.getMessage());
            }
        }
    }

    /**
     * 从输入流读取对象
     * @param is
     * @return
     */
    public static Serializable readObject(InputStream is) {
        Object object = null;
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new BufferedInputStream(is));
            object = ois.readObject();
        } catch (FileNotFoundException e) {
            Log.d(e.getMessage());
        } catch (StreamCorruptedException e) {
            Log.d(e.getMessage());
        } catch (IOException e) {
            Log.d(e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.d(e.getMessage());
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }

                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                Log.d(e.getMessage());
            }
        }
        return (Serializable) object;
    }

    /**
     * 将对象写入文件
     * @param obj
     * @param objPath
     */
    public static void writeObject(Serializable obj, String objPath) {

        File file = new File(objPath);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            writeObject(obj, os);
        } catch (FileNotFoundException e) {
            Log.d(e.getMessage());
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                Log.d(e.getMessage());
            }
        }
    }

    /**
     * 从文件读取对象
     * @param file
     * @return
     */
    public static Serializable readObject(File file) {
        if (!file.exists() || file.length() == 0) {
            return null;
        }
        Object object = null;
        InputStream is = null;
        ObjectInputStream ois = null;
        try {
            is = new FileInputStream(file);
            object = readObject(is);
        } catch (FileNotFoundException e) {
            Log.d(e.getMessage());
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
                if (is != null) {
                    is.close();
                }

            } catch (IOException e) {
                Log.d(e.getMessage());
            }
        }
        return (Serializable) object;
    }
}
