import java.io.*;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

public class Strip {
	private static Node getLast (NodeList list) {
		for (int i = list.getLength() - 1; i > 0; i--) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				return list.item(i);
			}
		}
		return list.item(0);
	}

	public static void main (String[] args) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(args[0]));
			Node l = getLast(doc.getDocumentElement().getChildNodes());
			l.removeChild(getLast(l.getChildNodes()));
			TransformerFactory.newInstance().newTransformer()
							  .transform(new DOMSource(doc), new StreamResult(new FileOutputStream(args[1])));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
