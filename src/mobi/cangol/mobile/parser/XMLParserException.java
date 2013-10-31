package mobi.cangol.mobile.parser;

import java.lang.reflect.Field;

import org.w3c.dom.Node;

public class XMLParserException extends ParserException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Node mNode;
	
	public XMLParserException(Throwable throwable) {
		super(throwable);
		mNode=null;
	}
	public XMLParserException(String message, Throwable throwable) {
		super(message, throwable);
	}
	public XMLParserException(Class<?> clazz,String message, Throwable throwable) {
		super(clazz,message, throwable);
	}
	
	public XMLParserException(Class<?> clazz,Field field,String message, Throwable throwable) {
		super(clazz,field,message,throwable);
	}
	
	public XMLParserException(Node node,String message, Throwable throwable) {
		super(message,throwable);
		mNode=node;
	}
	
	@Override
	public String getMessage() {
		if(mNode!=null){
			String nodeText=mNode.getTextContent();
	        if (nodeText != null) {
	            return "\nError '"+super.getMessage()+"' occurred in \n" +nodeText +"\n"+getCause().getMessage();
	        }else{
	        	return "\nError '"+super.getMessage()+"' occurred in "+ mNode.getNodeName()+"\n"+ getCause().getMessage();
	        }
		}else{
			return super.getMessage();
		}
	}
	
}
