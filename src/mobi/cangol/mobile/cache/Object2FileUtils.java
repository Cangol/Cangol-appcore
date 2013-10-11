package mobi.cangol.mobile.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.math.BigDecimal;

import org.json.JSONException;
import org.json.JSONObject;

import mobi.cangol.mobile.logging.Log;

import android.text.TextUtils;

public class Object2FileUtils {
	private final static String TAG="Object2FileUtils";
	private final static boolean DEBUG=true;
	
	
	public static void writeJSONObject2File(JSONObject jsonObject,String objPath){
		File file = new File(objPath);
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
			byte[] buffer = jsonObject.toString().getBytes("UTF-8");
			fileOutputStream.write(buffer);
			fileOutputStream.flush();			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(fileOutputStream!=null){
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fileOutputStream = null;
			}
		}
	}
	
	public static JSONObject readFile2JSONObject(File jsonFile){
		FileInputStream fileInputStream = null;
		String content = null;
		JSONObject jsonObject=null;
		try {
			fileInputStream = new FileInputStream(jsonFile);
			byte[] buffer =new byte[fileInputStream.available()];
			if(fileInputStream.read(buffer) != -1){
				content = new String(buffer,"UTF-8");
				if(!TextUtils.isEmpty(content))
					jsonObject=new JSONObject(content);
				
			}
		} catch (FileNotFoundException e) {
			if(DEBUG)Log.e("Exception", "FileNotFoundException "+jsonFile.getAbsolutePath(), e);
		} catch (IOException e) {
			if(DEBUG)Log.e("Exception", "IOException "+jsonFile.getAbsolutePath(), e);
		} catch (JSONException e) {
			if(DEBUG)Log.e("Exception", "JSONException "+jsonFile.getAbsolutePath(), e);
		} finally{
			if(fileInputStream != null){
				try {
					fileInputStream.close();
				} catch (IOException e) {
					if(DEBUG)Log.e("Exception", "IOException", e);
				}
				fileInputStream = null;
			}
		}
		return jsonObject;	
	}
	public static void writeObject(Object obj,OutputStream out) {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(out);
			oos.writeObject(obj);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(oos!=null)oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static Object readObject(InputStream is) {
		Object object = null;
		ObjectInputStream ois = null;
		try {
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
	//bt字节参考量
		private static final float SIZE_BT=1024L;
		//KB字节参考量
		private static final float SIZE_KB=SIZE_BT * 1024.0f;
		//MB字节参考量
		private static final float SIZE_MB=SIZE_KB * 1024.0f;
		//GB字节参考量
		private static final float SIZE_GB=SIZE_MB * 1024.0f;
		//TB字节参考量
		private static final float SIZE_TB=SIZE_GB * 1024.0f;
		
		private static final int SACLE=2;
		
		
		public static String getReadableSize(long length ) {
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
		public static long getFileLength(String filepath){
			File file=new File(filepath);
			if(file.exists())
				return file.length();
			return 0;
		}
		public static long getFileSize(File f){
	        long size = 0;
	        File flist[] = f.listFiles();
	        for (int i = 0; i < flist.length; i++) {
	            if (flist[i].isDirectory()) {
	                size = size + getFileSize(flist[i]);
	            } else {
	                size = size + flist[i].length();
	            }
	        }
	        return size;
	    }
		public static String getFileSizeString(File f){
			long size=getFileSize(f);
			return getReadableSize(size);
		}
}
