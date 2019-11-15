/**
 * Copyright (c) 2013 Cangol
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mobi.cangol.mobile.utils

/**
 * @author Cangol
 */

import android.annotation.SuppressLint
import android.text.TextUtils
import android.webkit.MimeTypeMap

import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.math.BigDecimal
import java.util.ArrayList

import mobi.cangol.mobile.logging.Log

object FileUtils {
    private const val TAG = "FileUtils"
    private const val UTF_8 = "UTF-8"

    /**
     * 删除文件，可以是单个文件或文件夹
     *
     * @param fileName 待删除的文件名
     * @return 文件删除成功返回true, 否则返回false
     */
    @JvmStatic fun delete(fileName: String): Boolean {
        val file = File(fileName)
        if (!file.exists()) {
            Log.d(TAG, "delete File fail! $fileName not exist")
            return false
        } else {
            return if (file.isFile) {
                deleteFile(fileName)
            } else {
                deleteDirectory(fileName)
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 被删除文件的文件名
     * @return 单个文件删除成功返回true, 否则返回false
     */
    @JvmStatic fun deleteFile(fileName: String): Boolean {
        val file = File(fileName)
        if (file.isFile && file.exists()) {
            if (file.delete())
                Log.d(TAG, "delete single file  success!$fileName")
            return true
        } else {
            Log.d(TAG, "delete single file fail!$fileName")
            return false
        }
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param dir 被删除目录的文件路径
     * @return 目录删除成功返回true, 否则返回false
     */
    @JvmStatic fun deleteDirectory(dir: String): Boolean {
        var dir = dir
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator
        }
        val dirFile = File(dir)
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory) {
            Log.d(TAG, "delete directory fail $dir not exist")
            return false
        }
        var flag = true
        // 删除文件夹下的所有文件(包括子目录)
        val files = dirFile.listFiles()
        for (i in files.indices) {
            // 删除子文件
            if (files[i].isFile) {
                flag = deleteFile(files[i].absolutePath)
                if (!flag) {
                    break
                }
            } else {
                flag = deleteDirectory(files[i].absolutePath)
                if (!flag) {
                    break
                }
            }// 删除子目录
        }

        if (!flag) {
            Log.d(TAG, "delete directory fail")
            return false
        }

        // 删除当前目录dirFile.delete()
        if (dirFile.delete()) {
            Log.d(TAG, "delete directory $dir success!")
            return true
        } else {
            Log.d(TAG, "delete directory $dir fail!")
            return false
        }
    }

    /**
     * 拷贝文件oldPath到newPath
     *
     * @param oldPath
     * @param newPath
     */
    @JvmStatic fun copyFile(oldPath: String, newPath: String) {
        var ins: InputStream? = null
        var fos: FileOutputStream? = null
        try {
            ins = FileInputStream(oldPath) // 读入原文件
            fos = FileOutputStream(newPath)
            val buffer = ByteArray(4094)
            var end:Boolean?=false
            while (end!!) {
                val len = ins.read(buffer)
                end = if(len>0){
                    fos.write(buffer, 0, len)
                    true
                }else{
                    false
                }
            }
        } catch (e: Exception) {
            Log.d("copy file error!" + e.message)
        } finally {
            if (ins != null) {
                try {
                    ins.close()
                } catch (e: IOException) {
                    Log.e(e.message)
                }

            }
            if (fos != null) {
                try {
                    fos.close()
                } catch (e: IOException) {
                    Log.e(e.message)
                }

            }
        }
    }

    /**
     * 新建目录
     *
     * @param folderPath String 如 c:/fqf
     */
    @JvmStatic fun newFolder(folderPath: String) {
        try {
            val myFilePath = File(folderPath)
            if (!myFilePath.exists()) {
                myFilePath.mkdirs()
            }
        } catch (e: Exception) {
            Log.d(e.message)
        }

    }

    /**
     * 新建文件
     *
     * @param filePath    String 文件路径及名称
     * @param fileContent String 文件内容
     */
    @JvmStatic fun newFile(filePath: String, fileContent: String) {
        var outWrite: OutputStreamWriter? = null
        var myFile: PrintWriter? = null
        try {
            val myFilePath = File(filePath)
            if (!myFilePath.exists() && !myFilePath.createNewFile()) {
                Log.d(TAG, "newFile createNewFile fail$filePath")
            }
            outWrite = OutputStreamWriter(FileOutputStream(myFilePath), UTF_8)
            myFile = PrintWriter(outWrite)
            myFile.println(fileContent)

        } catch (e: Exception) {
            Log.d(e.message)
        } finally {
            if (outWrite != null) {
                try {
                    outWrite.close()
                } catch (e: IOException) {
                    Log.e(e.message)
                }

            }
            myFile?.close()
        }

    }

    /**
     * 删除文件
     *
     * @param filePath String 文件路径及名称
     */
    @JvmStatic fun delFile(filePath: String) {
        try {
            val file = File(filePath)
            if (file.delete()) {
                Log.d(TAG, "delete file$filePath success!")
            }
        } catch (e: Exception) {
            Log.d(e.message)
        }

    }

    /**
     * 删除文件夹
     *
     * @param folderPath String 文件夹路径及名称 如c:/fqf
     */
    @JvmStatic fun delFolder(folderPath: String) {
        try {
            delAllFile(folderPath) // 删除完里面所有内容
            val file = File(folderPath)
            file.delete()
        } catch (e: Exception) {
            Log.d(e.message)
        }

    }

    /**
     * 删除文件夹里面的所有文件
     *
     * @param path String 文件夹路径 如 c:/fqf
     */
    @JvmStatic fun delAllFile(path: String) {
        val file = File(path)
        if (!file.exists()) {
            return
        }
        if (!file.isDirectory) {
            return
        }
        val tempList = file.list()
        var temp: File? = null
        for (i in tempList.indices) {
            if (path.endsWith(File.separator)) {
                temp = File(path + tempList[i])
            } else {
                temp = File(path + File.separator + tempList[i])
            }
            if (temp.isFile && !temp.delete()) {
                //do nothings
            }
            if (temp.isDirectory) {
                delAllFile(path + "/ " + tempList[i])// 先删除文件夹里面的文件
                delFolder(path + "/ " + tempList[i])// 再删除空文件夹
            }
        }
    }

    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     */
    @JvmStatic fun copyFolder(oldPath: String, newPath: String) {
        var input: FileInputStream? = null
        var output: FileOutputStream? = null
        try {
            File(newPath).mkdirs() // 如果文件夹不存在 则建立新文件夹
            val a = File(oldPath)
            val file = a.list()
            var temp: File? = null
            for (i in file.indices) {
                temp = if (oldPath.endsWith(File.separator)) {
                    File(oldPath + file[i])
                } else {
                    File(oldPath + File.separator + file[i])
                }

                if (temp.isFile) {
                    input = FileInputStream(temp)
                    output = FileOutputStream(newPath + "/ " + temp.name)
                    val buffer = ByteArray(1024 * 5)
                    var end:Boolean?=false
                    while (end!!) {
                        val len = input.read(buffer)
                        end = if(len>0){
                            output.write(buffer, 0, len)
                            true
                        }else{
                            false
                        }
                    }
                    output.flush()
                }

                if (temp.isDirectory) {
                    // 如果是子文件夹
                    copyFolder(oldPath + "/ " + file[i], newPath + "/ "+ file[i])
                }
            }
        } catch (e: Exception) {
            Log.d(e.message)
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (e: IOException) {
                    Log.e(e.message)
                }

            }
            if (output != null) {
                try {
                    output.close()
                } catch (e: IOException) {
                    Log.e(e.message)
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
    @JvmStatic fun moveFile(oldPath: String, newPath: String) {
        copyFile(oldPath, newPath)
        delFile(oldPath)

    }

    /**
     * 移动文件到指定目录
     *
     * @param oldPath String 如：c:/fqf.txt
     * @param newPath String 如：d:/fqf.txt
     */
    @JvmStatic fun moveFolder(oldPath: String, newPath: String) {
        copyFolder(oldPath, newPath)
        delFolder(oldPath)
    }

    /**
     * 按后缀遍历目录
     *
     * @param f
     * @param fileList
     * @param suffix
     * @return
     */
    @JvmStatic fun searchBySuffix(f: File, fileList: MutableList<File>?,
                       vararg suffix: String): List<File> {
        var fileList = fileList
        if (null == fileList) {
            fileList = ArrayList()
        }
        if (f.isDirectory) {
            val childs = f.listFiles()
            if (childs != null) {
                for (i in childs.indices) {
                    if (childs[i].isDirectory) {
                        searchBySuffix(childs[i], fileList, *suffix)
                    } else {
                        for (j in suffix.indices) {
                            if (childs[i].name.endsWith(suffix[j])) {
                                fileList.add(childs[i])
                            }
                        }

                    }
                }
            }
        }
        return fileList
    }

    /**
     * 将字符串写入文件
     *
     * @param file
     * @param content
     */
    @JvmStatic fun writeString(file: File, content: String) {
        var os: FileOutputStream? = null
        try {
            os = FileOutputStream(file)
            os.write(content.toByteArray(charset(UTF_8)))
            os.flush()
        } catch (e: IOException) {
            Log.d(e.message)
        } finally {
            if (os != null) {
                try {
                    os.close()
                } catch (e: IOException) {
                    Log.e(e.message)
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
    @JvmStatic fun writeObject(obj: Any, objPath: String) {
        var fos: FileOutputStream? = null
        var oos: ObjectOutputStream? = null
        try {
            fos = FileOutputStream(File(objPath))
            oos = ObjectOutputStream(fos)
            oos.writeObject(obj)
        } catch (e: Exception) {
            Log.d(e.message)
        } finally {
            try {
                oos?.close()
            } catch (e: IOException) {
                Log.d(e.message)
            }

            try {
                fos?.close()
            } catch (e: IOException) {
                Log.d(e.message)
            }

        }
    }

    /**
     * 从文件读取object
     *
     * @param file
     * @return
     */
    @JvmStatic fun readObject(file: File): Any? {
        if (!file.exists() || file.length() == 0L) {
            return null
        }
        var `object`: Any? = null
        var fos: FileInputStream? = null
        var ois: ObjectInputStream? = null
        try {
            fos = FileInputStream(file)
            ois = ObjectInputStream(fos)
            `object` = ois.readObject()
        } catch (e: Exception) {
            Log.d(e.message)
        } finally {
            try {
                ois?.close()
            } catch (e: IOException) {
                Log.d(e.message)
            }

            try {
                fos?.close()
            } catch (e: IOException) {
                Log.d(e.message)
            }

        }
        return `object`
    }

    /**
     * 输入流转string
     *
     * @param is
     * @return
     */
    @JvmStatic fun convertString(`is`: InputStream): String {
        val sb = StringBuilder()
        var readline = ""
        try {
            val br = BufferedReader(InputStreamReader(`is`, UTF_8))
            while (br.ready()) {
                readline = br.readLine()
                sb.append(readline)
            }
            br.close()
        } catch (ie: IOException) {
            Log.d(TAG, "converts failed.")
        }

        return sb.toString()
    }

    /**
     * 获取格式化的文件大小
     *
     * @param length
     * @return
     */
    @JvmStatic fun formatSize(length: Long): String {
        val sizeBt = 1024f
        val sizeKb = sizeBt * 1024.0f
        val sizeMb = sizeKb * 1024.0f
        val sizeGb = sizeMb * 1024.0f
        val scale = 2
        if (length >= 0 && length < sizeBt) {
            return (Math.round(length * 10.0f) / 10.0).toString() + "B"
        } else if (length >= sizeBt && length < sizeKb) {
            return (Math.round(length / sizeBt * 10.0f) / 10.0f).toString() + "KB"
        } else if (length >= sizeKb && length < sizeMb) {
            return (Math.round(length / sizeKb * 10.0f) / 10.0f).toString() + "MB"
        } else if (length >= sizeMb && length < sizeGb) {
            val longs = BigDecimal(java.lang.Double.valueOf(length.toString() + "")
                    .toString())
            val sizeMB = BigDecimal(java.lang.Double.valueOf(sizeMb.toString() + "")
                    .toString())
            val result = longs.divide(sizeMB, scale,
                    BigDecimal.ROUND_HALF_UP).toString()
            return result + "GB"
        } else {
            val longs = BigDecimal(java.lang.Double.valueOf(length.toString() + "")
                    .toString())
            val sizeMB = BigDecimal(java.lang.Double.valueOf(sizeMb.toString() + "")
                    .toString())
            val result = longs.divide(sizeMB, scale,
                    BigDecimal.ROUND_HALF_UP).toString()
            return result + "TB"
        }
    }

    @SuppressLint("DefaultLocale")
            /**
             * 将格式化的文件大小转换为long
             * @param sizeStr
             * @return
             */
    @JvmStatic fun formatSize(sizeStr: String?): Long {
        if (sizeStr != null && "" != sizeStr.trim { it <= ' ' }) {
            val unit = sizeStr
                    .replace("([1-9]+[0-9]*|0)(\\.[\\d]+)?".toRegex(), "")
            val size = sizeStr.substring(0, sizeStr.indexOf(unit))
            if (TextUtils.isEmpty(size)) {
                return -1
            }

            val s = java.lang.Float.parseFloat(size)
            if ("B".equals(unit, ignoreCase = true)) {
                return s.toLong()
            } else if ("KB".equals(unit, ignoreCase = true) || "K".equals(unit, ignoreCase = true)) {
                return (s * 1024).toLong()
            } else if ("MB".equals(unit, ignoreCase = true) || "M".equals(unit, ignoreCase = true)) {
                return (s * 1024f * 1024f).toLong()
            } else if ("GB".equals(unit, ignoreCase = true) || "G".equals(unit, ignoreCase = true)) {
                return (s * 1024f * 1024f * 1024f).toLong()
            } else if ("TB".equals(unit, ignoreCase = true) || "T".equals(unit, ignoreCase = true)) {
                return (s * 1024f * 1024f * 1024f * 1024f).toLong()
            }
        }
        return -1
    }

    /**
     * 获取文件类型或后缀
     *
     * @param filePath
     * @return
     */
    @JvmStatic fun getFileSuffix(filePath: String): String {
        val arrays = filePath.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return if (arrays.size >= 2) {
            arrays[arrays.size - 1]
        } else {
            "UNKNOWN"
        }
    }

    /**
     * 获取mimeType
     *
     * @param file
     * @return
     */
    @JvmStatic fun getMIMEType(file: File): String? {
        val suffix = file.name.substring(file.name.lastIndexOf('.') + 1, file.name.length).toLowerCase()
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix)
    }

    /**
     * 获取文件名
     *
     * @param filePath
     * @return
     */
    @JvmStatic fun getFileName(filePath: String): String {
        val fileName = File(filePath).name
        val arrays = filePath.split("\\_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return if (arrays.size >= 2) {
            arrays[arrays.size - 1]
        } else {
            fileName
        }
    }

    /**
     * 获取文件夹中总文件大小
     *
     * @param file
     * @return 文件大小 单位字节
     */
    @JvmStatic fun getFolderSize(file: File): Long {
        var size: Long = 0
        try {
            val fileList = file.listFiles()
            for (i in fileList.indices) {
                if (fileList[i].isDirectory) {
                    size = size + getFolderSize(fileList[i])
                } else {
                    size = size + fileList[i].length()
                }
            }
        } catch (e: Exception) {
            Log.d(e.message)
        }

        return size
    }
}
