package mobi.cangol.mobile.parser;

import java.lang.reflect.Field;

import org.json.JSONObject;


public class JSONParserException extends ParserException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JSONObject mNode;
	
	public JSONParserException(String message,Throwable throwable) {
		super(throwable);
	}
	public JSONParserException(Class<?> clazz,String message, Throwable throwable) {
		super(clazz, message,throwable);
	}
	
	public JSONParserException(Class<?> clazz,Field field,String message, Throwable throwable) {
		super(clazz,field,message,throwable);
	}
	
	public JSONParserException(JSONObject node,String message, Throwable throwable) {
		super(throwable);
		mNode=node;
	}
	@Override
	public String getMessage() {
		if(mNode!=null){
			String nodeText=mNode.toString();
	        return "\nError '"+super.getMessage()+"' occurred in \n" +nodeText +"\n"+ getCause().getMessage();
		}else{
			return super.getMessage();
		}
	}
}
