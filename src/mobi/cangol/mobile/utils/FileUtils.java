/** 
 * Copyright (c) 2013 Cangol
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.utils;

/**
 * @Description:文件工具类
 * @version $Revision: 1.0 $
 * @author Cangol
 * @date: 2010-12-10
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StreamCorruptedException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.text.TextUtils;

public class FileUtils {
	/**
	 * 删除文件，可以是单个文件或文件夹
	 * 
	 * @param fileName
	 *            待删除的文件名
	 * @return 文件删除成功返回true,否则返回false
	 */
	public static boolean delete(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			System.out.println("delete File fail! " + fileName + " not exist");
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
	 * @param fileName
	 *            被删除文件的文件名
	 * @return 单个文件删除成功返回true,否则返回false
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		if (file.isFile() && file.exists()) {
			file.delete();
			System.out.println("delete single file" + fileName + " success!");
			return true;
		} else {
			System.out.println("delete single file" + fileName + " fail!");
			return false;
		}
	}

	/**
	 * 删除目录（文件夹）以及目录下的文件
	 * 
	 * @param dir
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true,否则返回false
	 */
	public static boolean deleteDirectory(String dir) {
		// 如果dir不以文件分隔符结尾，自动添加文件分隔符
		if (!dir.endsWith(File.separator)) {
			dir = dir + File.separator;
		}
		File dirFile = new File(dir);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			System.out.println("delete directory fail " + dir + " not exist");
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
			System.out.println("delete directory fail");
			return false;
		}

		// 删除当前目录
		if (dirFile.delete()) {
			System.out.println("delete directory " + dir + " success!");
			return true;
		} else {
			System.out.println("delete directory " + dir + " fail!");
			return false;
		}
	}

	public static void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("copy file error!");
			e.printStackTrace();
		}
	}

	/**
	 * 新建目录
	 * 
	 * @param folderPath
	 *            String 如 c:/fqf
	 * @return boolean
	 */
	public static void newFolder(String folderPath) {
		try {
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.mkdir();
			}
		} catch (Exception e) {
			System.out.println("新建目录操作出错 ");
			e.printStackTrace();
		}
	}

	/**
	 * 新建文件
	 * 
	 * @param filePathAndName
	 *            String 文件路径及名称 如c:/fqf.txt
	 * @param fileContent
	 *            String 文件内容
	 * @return boolean
	 */
	public static void newFile(String filePathAndName, String fileContent) {

		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.createNewFile();
			}
			FileWriter resultFile = new FileWriter(myFilePath);
			PrintWriter myFile = new PrintWriter(resultFile);
			String strContent = fileContent;
			myFile.println(strContent);
			resultFile.close();

		} catch (Exception e) {
			System.out.println("新建目录操作出错 ");
			e.printStackTrace();

		}

	}

	/**
	 * 删除文件
	 * 
	 * @param filePathAndName
	 *            String 文件路径及名称 如c:/fqf.txt
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public static void delFile(String filePathAndName) {
		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			java.io.File myDelFile = new java.io.File(filePath);
			myDelFile.delete();

		} catch (Exception e) {
			System.out.println("删除文件操作出错 ");
			e.printStackTrace();

		}

	}

	/**
	 * 删除文件夹
	 * 
	 * @param filePathAndName
	 *            String 文件夹路径及名称 如c:/fqf
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹

		} catch (Exception e) {
			System.out.println("删除文件夹操作出错 ");
			e.printStackTrace();

		}

	}

	/**
	 * 删除文件夹里面的所有文件
	 * 
	 * @param path
	 *            String 文件夹路径 如 c:/fqf
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
				temp.delete();
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
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf/ff
	 * @return boolean
	 */
	public static void copyFolder(String oldPath, String newPath) {

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
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath
							+ "/ " + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(oldPath + "/ " + file[i], newPath + "/ "
							+ file[i]);
				}
			}
		} catch (Exception e) {
			System.out.println("复制整个文件夹内容操作出错 ");
			e.printStackTrace();

		}

	}
	/**
	 * 移动文件到指定目录
	 * 
	 * @param oldPath
	 *            String 如：c:/fqf.txt
	 * @param newPath
	 *            String 如：d:/fqf.txt
	 */
	public static void moveFile(String oldPath, String newPath) {
		copyFile(oldPath, newPath);
		delFile(oldPath);

	}
	/**
	 * 移动文件到指定目录
	 * 
	 * @param oldPath
	 *            String 如：c:/fqf.txt
	 * @param newPath
	 *            String 如：d:/fqf.txt
	 */
	public static void moveFolder(String oldPath, String newPath) {
		copyFolder(oldPath, newPath);
		delFolder(oldPath);
	}

	/**
	 * 按后缀遍历目录
	 * @param f
	 * @param fileList
	 * @param suffix
	 * @return
	 */
	public static List<File> searchBySuffix(File f,List<File> fileList,String... suffix){
		if(null==fileList)fileList=new ArrayList<File>();
		if(f.isDirectory()){ 
			File[] childs = f.listFiles();   
			if(childs!=null)for (int i = 0; i < childs.length; i++) {   
				if (childs[i].isDirectory()) {  
					searchBySuffix(childs[i],fileList,suffix);
				} else {
					for(int j=0;j<suffix.length;j++){
						if(childs[i].getName().endsWith(suffix[j])){
							fileList.add(childs[i]);
						}
					}
					
				}
			}  
		}
		return fileList;
	}
	/**
	 * 将字符串写入文件
	 * @param file
	 * @param content
	 */
	public static void writeStr(File file,String content){
		FileOutputStream os = null;
		try {
			byte[] buffer = content.getBytes("UTF-8");
			os = new FileOutputStream(file);
			os.write(buffer);
			os.flush();			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(os!=null){
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				os = null;
			}
		}
	}
	/**
	 * 将object写入文件
	 * @param obj
	 * @param objPath
	 */
	public static void writeObject(Object obj,String objPath) {

		File file = new File(objPath);
		//if (file.exists())file.delete();
		FileOutputStream os = null;
		ObjectOutputStream oos = null;
		try {
			os = new FileOutputStream(file);
			oos = new ObjectOutputStream(os);
			oos.writeObject(obj);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(oos!=null)oos.close();
				if(os!=null)os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 从文件读取object
	 * @param file
	 * @return
	 */
	public static Object readObject(File file) {
		if(!file.exists()||file.length()==0)return null;
		Object object = null;
		InputStream is = null;
		ObjectInputStream ois = null;
		try {
			is = new FileInputStream(file);
			ois = new ObjectInputStream(is);
			object = ois.readObject();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if(ois!=null)ois.close();
				if(is!=null)is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return object;
	}
	/**
	 * 输入流转string
	 * @param is
	 * @return
	 */
	public static String converts(InputStream is){
        StringBuilder sb = new StringBuilder();
        String readline = "";
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while (br.ready())
            {
                readline = br.readLine();
                sb.append(readline);
            }
            br.close();
        } catch (IOException ie)
        {
            System.out.println("converts failed.");
        }
        return sb.toString();
    }
	/**
	 * 获取格式化的文件大小
	 * @param length
	 * @return
	 */
	public static String getSize(long length) {
		float SIZE_BT=1024L;
		float SIZE_KB=SIZE_BT * 1024.0f;
		float SIZE_MB=SIZE_KB * 1024.0f;
		float SIZE_GB=SIZE_MB * 1024.0f;
		float SIZE_TB=SIZE_GB * 1024.0f;
		int SACLE=2;
		if(length>=0 && length < SIZE_BT) {
			return (double)(Math.round(length*10)/10.0)  +"B";
		} else if(length>=SIZE_BT&&length<SIZE_KB) {
			return (double)(Math.round((length/SIZE_BT)*10)/10.0)  +"KB";//length/SIZE_BT+"KB";
		} else if(length>=SIZE_KB&&length<SIZE_MB) {
			return (double)(Math.round((length/SIZE_KB)*10)/10.0)  +"MB";//length/SIZE_KB+"MB";
		} else if(length>=SIZE_MB&&length<SIZE_GB) {
			BigDecimal longs=new BigDecimal(Double.valueOf(length+"").toString());
			BigDecimal sizeMB=new BigDecimal(Double.valueOf(SIZE_MB+"").toString());
			String result=longs.divide(sizeMB, SACLE,BigDecimal.ROUND_HALF_UP).toString();
			//double result=this.length/(double)SIZE_MB;
			return result+"GB";
		} else {
			BigDecimal longs=new BigDecimal(Double.valueOf(length+"").toString());
			BigDecimal sizeMB=new BigDecimal(Double.valueOf(SIZE_GB+"").toString());
			String result=longs.divide(sizeMB, SACLE,BigDecimal.ROUND_HALF_UP).toString();
			return result+"TB";
		}
	}
	@SuppressLint("DefaultLocale")
	/**
	 * 将格式化的文件大小转换为long
	 * @param sizeStr
	 * @return
	 */
	public static long getSize(String sizeStr){
		if(sizeStr!=null&&sizeStr.trim().length()>0){
			String unit=sizeStr.replaceAll("([1-9]+[0-9]*|0)(\\.[\\d]+)?", "");
			String size=sizeStr.substring(0, sizeStr.indexOf(unit));
			if(TextUtils.isEmpty(size))return -1;
			float s=Float.parseFloat(size);
			if("B".equals(unit.toLowerCase())){
				return (long) s;
			}else if("KB".equals(unit.toLowerCase())||"K".equals(unit.toLowerCase())){
				return (long) (s*1024);
			}else if("MB".equals(unit.toLowerCase())||"M".equals(unit.toLowerCase())){
				return (long) (s*1024*1024);
			}else if("GB".equals(unit.toLowerCase())||"G".equals(unit.toLowerCase())){
				return (long) (s*1024*1024*1024);
			}else if("TB".equals(unit.toLowerCase())||"T".equals(unit.toLowerCase())){
				return (long) (s*1024*1024*1024*1024);
			}	
		}
		return -1;
	}
}
