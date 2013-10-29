package mobi.cangol.mobile.json;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.util.Log;

public class DocumentParser {
	private DocumentBuilderFactory factory;
	private Element root;
	public DocumentParser(InputStream is){
		factory=DocumentBuilderFactory.newInstance();
		root=parseRoot(is);
	}
	private Element parseRoot(InputStream is){
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document document=builder.parse(is);
			return document.getDocumentElement();
		} catch (ParserConfigurationException e) {	
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public Element getRoot() {
		return root;
	}
	
	public String getNodeValue(String... nodeName){
		return getNodeValue(root,nodeName);
	}
	
	public static String getNodeAttr(Node node,String attrName){
		Element element=((Element)node);
		return element.getAttribute(attrName);
	}
	
	public static String getNodeValue(Node parent,String... nodeName){
		NodeList nodeList=((Element)parent).getElementsByTagName(nodeName[0]);
		if(null==nodeList){
			return null;
		}else {
			if(nodeList.getLength()>0){
				Node node=nodeList.item(0);
				if(nodeName.length==1){
					return node.getTextContent();
				}else{
					String[] nodeNs=new String[nodeName.length-1];
					for(int i=1;i<nodeName.length;i++){
						nodeNs[i-1]=nodeName[i];
					}
					return getNodeValue((Element) node,nodeNs);
				}
			}else
				return null;
			
		}
	}
	public static NodeList getNodeList(Node parent,String nodeName){
		return ((Element)parent).getElementsByTagName(nodeName);
	}
	
	public static Node getNode(Node parent,String nodeName){
		NodeList nodeList=((Element)parent).getElementsByTagName(nodeName);
		if(null==nodeList){
			return null;
		}else {
			return nodeList.item(0);
		}
	}

}
