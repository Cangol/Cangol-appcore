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

/**
 * @author Cangol
 */

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import mobi.cangol.mobile.logging.Log;

public class FileUtils {
    public static final String TAG = "FileUtils";
    public static final String UTF_8 = "UTF-8";

    private FileUtils() {
    }

    /**
     * 删除文件，可以是单个文件或文件夹
     *
     * @param fileName 待删除的文件名
     * @return 文件删除成功返回true, 否则返回false
     */
    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            Log.d(TAG, "delete File fail! " + fileName + " not exist");
            return false;
        } else {
            if (file.isFile()) {
                return deleteFile(fileName);
            } else {
                return deleteDirectory(fileName);
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 被删除文件的文件名
     * @return 单个文件删除成功返回true, 否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.isFile() && file.exists()) {
            if(file.delete())
                Log.d(TAG, "delete single file  success!" + fileName );
            return true;
        } else {
            Log.d(TAG, "delete single file fail!" + fileName);
            return false;
        }
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param dir 被删除目录的文件路径
     * @return 目录删除成功返回true, 否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            Log.d(TAG, "delete directory fail " + dir + " not exist");
            return false;
        }
        boolean flag = true;
        // 删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
            // 删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }

        if (!flag) {
            Log.d(TAG, "delete directory fail");
            return false;
        }

        // 删除当前目录dirFile.delete()
        if (dirFile.delete()) {
            Log.d(TAG, "delete directory " + dir + " success!");
            return true;
        } else {
            Log.d(TAG, "delete directory " + dir + " fail!");
            return false;
        }
    }

    /**
     * 拷贝文件oldPath到newPath
     *
     * @param oldPath
     * @param newPath
     */
    public static void copyFile(String oldPath, String newPath) {
        InputStream is=null;
        FileOutputStream fos=null;
        try {
            int byteRead = 0;
            is = new FileInputStream(oldPath); // 读入原文件
            fos = new FileOutputStream(newPath);
            byte[] buffer = new byte[4094];
            while ((byteRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteRead);
            }
        } catch (Exception e) {
            Log.d("copy file error!" + e.getMessage());
        }finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    Log.e(e.getMessage());
                }
            }
            if(fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e(e.getMessage());
                }
            }
        }
    }

    /**
     * 新建目录
     *
     * @param folderPath String 如 c:/fqf
     */
    public static void newFolder(String folderPath) {
        try {
            File myFilePath = new File(folderPath);
            if (!myFilePath.exists()) {
                myFilePath.mkdirs();
            }
        } catch (Exception e) {
            Log.d(e.getMessage());
        }
    }

    /**
     * 新建文件
     *
     * @param filePath    String 文件路径及名称
     * @param fileContent String 文件内容
     */
    public static void newFile(String filePath, String fileContent) {
        OutputStreamWriter outWrite =null;
        PrintWriter myFile=null;
        try {
            File myFilePath = new File(filePath);
            if (!myFilePath.exists()) {
                if(!myFilePath.createNewFile()){
                    Log.d(TAG, "newFile createNewFile fail" + filePath);
                }
            }
            outWrite = new OutputStreamWriter(new FileOutputStream(myFilePath), UTF_8);
            myFile = new PrintWriter(outWrite);
            myFile.println(fileContent);

        } catch (Exception e) {
            Log.d(e.getMessage());
        }finally {
            if(outWrite!=null){
                try {
                    outWrite.close();
                } catch (IOException e) {
                    Log.e(e.getMessage());
                }
            }
            if(myFile!=null){
                myFile.close();
            }
        }

    }

    /**
     * 删除文件
     *
     * @param filePath String 文件路径及名称
     */
    public static void delFile(String filePath) {
        try {
            File file = new File(filePath);
            if(file.delete()){
                Log.d(TAG, "delete file" + filePath + " success!");
            }
        } catch (Exception e) {
            Log.d(e.getMessage());
        }

    }

    /**
     * 删除文件夹
     *
     * @param folderPath String 文件夹路径及名称 如c:/fqf
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            File file = new File(folderPath);
            file.delete();
        } catch (Exception e) {
            Log.d(e.getMessage());
        }

    }

    /**
     * 删除文件夹里面的所有文件
     *
     * @param path String 文件夹路径 如 c:/fqf
     */
    public static void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                if (!temp.delete()) {
                    //do nothings
                }
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/ " + tempList[i]);// 先删除文件夹里面的文件
                delFolder(path + "/ " + tempList[i]);// 再删除空文件夹
            }
        }
    }

    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     */
    public static void copyFolder(String oldPath, String newPath) {
        FileInputStream input=null;
        FileOutputStream output =null;
        try {
            (new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                     input = new FileInputStream(temp);
                     output = new FileOutputStream(newPath+ "/ " + (temp.getName()));
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                }
                if (temp.isDirectory()) {// 如果是子文件夹
                    copyFolder(oldPath + "/ " + file[i], newPath + "/ "
                            + file[i]);
                }
            }
        } catch (Exception e) {
            Log.d(e.getMessage());
        }finally {
            if(input!=null){
                try {
                    input.close();
                } catch (IOException e) {
                    Log.e(e.getMessage());
                }
            }
            if(output!=null){
                try {
                    output.close();
                } catch (IOException e) {
                   Log.e(e.getMessage());
                }
            }
        }

    }

    /**
     * 移动文件到指定目录
     *
     * @param oldPath String 如：c:/fqf.txt
     * @param newPath String 如：d:/fqf.txt
     */
    public static void moveFile(String oldPath, String newPath) {
        copyFile(oldPath, newPath);
        delFile(oldPath);

    }

    /**
     * 移动文件到指定目录
     *
     * @param oldPath String 如：c:/fqf.txt
     * @param newPath String 如：d:/fqf.txt
     */
    public static void moveFolder(String oldPath, String newPath) {
        copyFolder(oldPath, newPath);
        delFolder(oldPath);
    }

    /**
     * 按后缀遍历目录
     *
     * @param f
     * @param fileList
     * @param suffix
     * @return
     */
    public static List<File> searchBySuffix(File f, List<File> fileList,
                                            String... suffix) {
        if (null == fileList) {
            fileList = new ArrayList<>();
        }
        if (f.isDirectory()) {
            File[] childs = f.listFiles();
            if (childs != null) {
                for (int i = 0; i < childs.length; i++) {
                    if (childs[i].isDirectory()) {
                        searchBySuffix(childs[i], fileList, suffix);
                    } else {
                        for (int j = 0; j < suffix.length; j++) {
                            if (childs[i].getName().endsWith(suffix[j])) {
                                fileList.add(childs[i]);
                            }
                        }

                    }
                }
            }
        }
        return fileList;
    }

    /**
     * 将字符串写入文件
     *
     * @param file
     * @param content
     */
    public static void writeString(File file, String content) {
        FileOutputStream os=null;
        try {
            os = new FileOutputStream(file);
            os.write(content.getBytes(UTF_8));
            os.flush();
        } catch (IOException e) {
            Log.d(e.getMessage());
        }finally {
            if(os!=null){
                try {
                    os.close();
                } catch (IOException e) {
                   Log.e(e.getMessage());
                }
            }
        }
    }

    /**
     * 将object写入文件
     *
     * @param obj
     * @param objPath
     */
    public static void writeObject(Object obj, String objPath) {
        FileOutputStream fos=null;
        ObjectOutputStream oos = null;
        try {
            fos=new FileOutputStream(new File(objPath));
            oos = new ObjectOutputStream(fos);
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
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                Log.d(e.getMessage());
            }
        }
    }

    /**
     * 从文件读取object
     *
     * @param file
     * @return
     */
    public static Object readObject(File file) {
        if (!file.exists() || file.length() == 0) {
            return null;
        }
        Object object = null;
        FileInputStream fos=null;
        ObjectInputStream ois = null;
        try {
            fos=new FileInputStream(file);
            ois = new ObjectInputStream(fos);
            object = ois.readObject();
        } catch (Exception e) {
            Log.d(e.getMessage());
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
                Log.d(e.getMessage());
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                Log.d(e.getMessage());
            }
        }
        return object;
    }

    /**
     * 输入流转string
     *
     * @param is
     * @return
     */
    public static String convertString(InputStream is) {
        StringBuilder sb = new StringBuilder();
        String readline = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, UTF_8));
            while (br.ready()) {
                readline = br.readLine();
                sb.append(readline);
            }
            br.close();
        } catch (IOException ie) {
            Log.d(TAG, "converts failed.");
        }
        return sb.toString();
    }

    /**
     * 获取格式化的文件大小
     *
     * @param length
     * @return
     */
    public static String formatSize(long length) {
        float sizeBt = 1024L;
        float sizeKb = sizeBt * 1024.0f;
        float sizeMb = sizeKb * 1024.0f;
        float sizeGb = sizeMb * 1024.0f;
        int scale = 2;
        if (length >= 0 && length < sizeBt) {
            return Math.round(length * 10.0f) / 10.0 + "B";
        } else if (length >= sizeBt && length < sizeKb) {
            return Math.round((length / sizeBt) * 10.0f) / 10.0f + "KB";
        } else if (length >= sizeKb && length < sizeMb) {
            return Math.round((length / sizeKb) * 10.0f) / 10.0f + "MB";
        } else if (length >= sizeMb && length < sizeGb) {
            BigDecimal longs = new BigDecimal(Double.valueOf(length + "")
                    .toString());
            BigDecimal sizeMB = new BigDecimal(Double.valueOf(sizeMb + "")
                    .toString());
            String result = longs.divide(sizeMB, scale,
                    BigDecimal.ROUND_HALF_UP).toString();
            return result + "GB";
        } else {
            BigDecimal longs = new BigDecimal(Double.valueOf(length + "")
                    .toString());
            BigDecimal sizeMB = new BigDecimal(Double.valueOf(sizeMb + "")
                    .toString());
            String result = longs.divide(sizeMB, scale,
                    BigDecimal.ROUND_HALF_UP).toString();
            return result + "TB";
        }
    }

    @SuppressLint("DefaultLocale")
    /**
     * 将格式化的文件大小转换为long
     * @param sizeStr
     * @return
     */
    public static long formatSize(String sizeStr) {
        if (sizeStr != null && !"".equals(sizeStr.trim())) {
            String unit = sizeStr
                    .replaceAll("([1-9]+[0-9]*|0)(\\.[\\d]+)?", "");
            String size = sizeStr.substring(0, sizeStr.indexOf(unit));
            if (TextUtils.isEmpty(size)) {
                return -1;
            }

            float s = Float.parseFloat(size);
            if ("B".equalsIgnoreCase(unit)) {
                return (long) s;
            } else if ("KB".equalsIgnoreCase(unit)
                    || "K".equalsIgnoreCase(unit)) {
                return (long) (s * 1024);
            } else if ("MB".equalsIgnoreCase(unit)
                    || "M".equalsIgnoreCase(unit)) {
                return (long) (s * 1024 * 1024);
            } else if ("GB".equalsIgnoreCase(unit)
                    || "G".equalsIgnoreCase(unit)) {
                return (long) (s * 1024 * 1024 * 1024);
            } else if ("TB".equalsIgnoreCase(unit)
                    || "T".equalsIgnoreCase(unit)) {
                return (long) (s * 1024 * 1024 * 1024 * 1024);
            }
        }
        return -1;
    }
    /**
     * 获取文件类型或后缀
     * @param filePath
     * @return
     */
    public static String getFileSuffix(String filePath) {
        String[] arrays = filePath.split("\\.");
        if (arrays.length >= 2) {
            return arrays[arrays.length - 1];
        } else {
            return "UNKNOWN";
        }
    }

    /**
     * 获取mimeType
     * @param file
     * @return
     */
    public static String getMIMEType(File file) {
        String suffix = file.getName().substring(file.getName().lastIndexOf('.') + 1, file.getName().length()).toLowerCase();
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);
    }

    /**
     * 获取文件名
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        String fileName = new File(filePath).getName();
        String[] arrays = filePath.split("\\_");
        if (arrays.length >= 2) {
            return arrays[arrays.length - 1];
        } else {
            return fileName;
        }
    }
    /**
     * 获取文件夹中总文件大小
     *
     * @param file
     * @return 文件大小 单位字节
     */
    public static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            Log.d(e.getMessage());
        }
        return size;
    }
}
