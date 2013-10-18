package mobi.cangol.mobile.utils;

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
import java.io.StreamCorruptedException;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
/**
 * @Description:
 * @version $Revision: 1.0 $
 * @author xuewu.wei
 */
public class Object2FileUtils {
	//
	public static void writeJSONObject2File(JSONObject jsonObject,String objPath){
		File file = new File(objPath);
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
			writeJSONObject(jsonObject,fileOutputStream);	
		} catch (FileNotFoundException e) {
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
		JSONObject jsonObject=null;
		try {
			fileInputStream = new FileInputStream(jsonFile);
			jsonObject=readJSONObject(fileInputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally{
			if(fileInputStream != null){
				try {
					fileInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fileInputStream = null;
			}
		}
		return jsonObject;	
	}
	
	public static void writeJSONObject(JSONObject jsonObject,OutputStream os){
		try {
			byte[] buffer = jsonObject.toString().getBytes("UTF-8");
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
	
	public static JSONObject readJSONObject(InputStream is){
		String content = null;
		JSONObject jsonObject=null;
		try {
			byte[] buffer =new byte[is.available()];
			if(is.read(buffer) != -1){
				content = new String(buffer,"UTF-8");
				if(!TextUtils.isEmpty(content))
					jsonObject=new JSONObject(content);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally{
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				is = null;
			}
		}
		return jsonObject;	
	}
	
	public static void writeObject(Object obj,OutputStream out) {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new BufferedOutputStream(out));
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
			ois = new ObjectInputStream(new BufferedInputStream(is));
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
		if (file.exists())file.delete();
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(file);
			writeObject(obj,os);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
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
			object = readObject(is);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
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
}
