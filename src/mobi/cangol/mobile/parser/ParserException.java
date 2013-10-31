package mobi.cangol.mobile.parser;

import java.lang.reflect.Field;

public class ParserException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Field mField;
	private Class<?> mClass;
	
	public ParserException(String message) {
		super(message);
	}

	public ParserException(Throwable throwable) {
		super(throwable);
	}

	public ParserException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public ParserException(Class<?> clazz,String message, Throwable throwable) {
		super(message, throwable);
		mField=null;
		mClass=clazz;
	}
	
	public ParserException(Class<?> clazz,Field field,String message, Throwable throwable) {
		super(message, throwable);
		mField=field;
		mClass=clazz;
	}
	
	@Override
	public String getMessage() {
		if(mClass!=null){
			if(mField!=null){
				String fieldName=mField.getName();
		        return "\nError '"+super.getMessage()+"' occurred in " +mClass.getName()+"@"+fieldName +"\n"+ getCause().getMessage();
			}else{
				return "\nError '"+super.getMessage()+"' occurred in " +mClass.getName() +"\n"+ getCause().getMessage();
			}
		}else{
			return super.getMessage();
		}
	}
}
