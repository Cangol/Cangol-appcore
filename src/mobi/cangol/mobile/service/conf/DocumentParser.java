package mobi.cangol.mobile.service.conf;

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
	private String getNodeValue(Element parent,String... nodeName){
		NodeList nodeList=parent.getElementsByTagName(nodeName[0]);
		Node node=nodeList.item(0);
		if(nodeName.length==1){
			return node.getTextContent();
		}else{
			String[] nodes=new String[nodeName.length-1];
			for(int i=1;i<nodeName.length;i++){
				nodes[i-1]=nodeName[i];
			}
			return getNodeValue((Element) node,nodes);
		}
	}
	public Element getElement(String nodeName){
		NodeList nodeList=root.getElementsByTagName(nodeName);
		Element element=(Element) nodeList.item(0);
		return element;
	}

}
