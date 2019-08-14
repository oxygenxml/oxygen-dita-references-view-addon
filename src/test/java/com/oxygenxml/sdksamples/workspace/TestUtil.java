package com.oxygenxml.sdksamples.workspace;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class TestUtil {	

	/**
	 * The XPath expression with all possible references available in the current
	 * DITA content.
	 */
	protected static final String ALL_REFS_XPATH_EXPRESSION = "/* | //*[contains(@class, ' topic/image ')] | //*[contains(@class, ' topic/xref ')]"
			+ " | //*[contains(@class, ' topic/link ')] | //*[@conref] | //*[@conkeyref] | //*[@keyref  and not(contains(@class, ' topic/image ')) "
			+ "and not(contains(@class, ' topic/link '))  and  not(contains(@class, ' topic/xref '))]";
	
	/**
	 * Evaluate the xPath expression in case of a DITA topic.
	 * 
	 * @param ditaContent The DITA Content
	 * @return The XPath nodes
	 */
	static Object[] evaluateAllRefsExpression(String ditaContent) {
		try {
			XPathExpression xpath = XPathFactory.newInstance().newXPath()
					.compile(ALL_REFS_XPATH_EXPRESSION);
			NodeList nodeList = (NodeList) xpath.evaluate(new InputSource(new StringReader(ditaContent)),
					XPathConstants.NODESET);
			Object[] nodes = new Object[nodeList.getLength()];
			for (int i = 0; i < nodes.length; i++) {
				nodes[i] = nodeList.item(i);
			}
			return nodes;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * Simulate double click in tree. This expects a node to be already selected.
	 * 
	 * @param tree The references tree.
	 */
	static void simulateDoubleClick(JTree tree) {
		Rectangle rowBounds = tree.getRowBounds(tree.getSelectionRows()[0]);
		Point point = new Point(rowBounds.x, rowBounds.y);
		MouseEvent mouseEvent = new MouseEvent(tree, MouseEvent.MOUSE_CLICKED, 0, 0, point.x, point.y, 2, false);
		tree.dispatchEvent(mouseEvent);
	}
	
	/**
	 * Create an array of AuthorElements evaluated by an XPath expression for the
	 * outgoing references.
	 * 
	 * @return a references array of AuthorElements
	 */
	static AuthorElementAdapter[] createAuthorElementArray() {
		Map<String, String> rootAttributeMap = new LinkedHashMap<String, String>();
		rootAttributeMap.put("class", "- topic/topic ");
		rootAttributeMap.put("id", "sample");
		AuthorElementAdapter rootElem = new AuthorElementAdapter("topic", 1, 342, rootAttributeMap);

		Map<String, String> imageAttributeMap = new LinkedHashMap<String, String>();
		imageAttributeMap.put("href", "image2.jpg");
		imageAttributeMap.put("class", "- topic/image ");
		AuthorElementAdapter imageElem = new AuthorElementAdapter("image", 49, 50, imageAttributeMap);

		Map<String, String> keyrefAttributeMap = new LinkedHashMap<String, String>();
		keyrefAttributeMap.put("href", "http://www.google.com");
		keyrefAttributeMap.put("format", "html");
		keyrefAttributeMap.put("scope", "external");
		keyrefAttributeMap.put("class", "- topic/xref ");
		AuthorElementAdapter keyrefElem = new AuthorElementAdapter("xref", 125, 126, keyrefAttributeMap);

		Map<String, String> googleAttributeMap = new LinkedHashMap<String, String>();
		googleAttributeMap.put("keyref", "google");
		googleAttributeMap.put("class", "- topic/xref ");
		AuthorElementAdapter googleKeyrefElem = new AuthorElementAdapter("xref", 203, 204, googleAttributeMap);

		Map<String, String> conrefAttributeMap = new LinkedHashMap<String, String>();
		conrefAttributeMap.put("conref", "sample2.dita#sample2/i1");
		conrefAttributeMap.put("class", "- topic/li ");
		AuthorElementAdapter conrefElem = new AuthorElementAdapter("li", 237, 247, conrefAttributeMap);

		Map<String, String> conkeyrefAttributeMap = new LinkedHashMap<String, String>();
		conkeyrefAttributeMap.put("conkeyref", "sample2/i1");
		conkeyrefAttributeMap.put("class", "- topic/li ");
		AuthorElementAdapter conkeyrefElem = new AuthorElementAdapter("li", 281, 291, conkeyrefAttributeMap);

		Map<String, String> linkAttributeMap = new LinkedHashMap<String, String>();
		linkAttributeMap.put("keyref", "myPDF");
		linkAttributeMap.put("class", "- topic/link ");
		AuthorElementAdapter linkElem = new AuthorElementAdapter("link", 321, 339, linkAttributeMap);

		AuthorElementAdapter[] elemArray = new AuthorElementAdapter[] 
				{ rootElem, imageElem, keyrefElem, googleKeyrefElem, conrefElem, conkeyrefElem, linkElem };

		return elemArray;
	}


	/**
	 * Dumps tree information.
	 *
	 * @param tree The tree to dump info for.
	 */
	static String dumpTree(ReferencesTree tree) {
		StringBuilder dump = new StringBuilder();

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
		dumpRecursive((DefaultTreeModel) tree.getModel(), root, dump);

		return dump.toString();
	}

	/**
	 * Builds tree representation recursively.
	 *
	 * @param node Current processed node.
	 */
	private static void dumpRecursive(DefaultTreeModel model, DefaultMutableTreeNode node, StringBuilder dump) {
		dump.append("\n").append(Arrays.toString(model.getPathToRoot(node)));
		@SuppressWarnings("unchecked")
		Enumeration<DefaultMutableTreeNode> children = node.children();
		while (children != null && children.hasMoreElements()) {
			dumpRecursive(model, children.nextElement(), dump);
		}
	}

	/**
	 * Show logging for the tree nodes as shown in the Oxygen editor.
	 * 
	 * @param tree The ReferencesTree
	 */
	static String logTreeNodes(ReferencesTree tree) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < tree.getRowCount(); i++) {
			TreePath path = tree.getPathForRow(i);
			JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(
					tree, path.getLastPathComponent(), false, true, true, i, false);
			stringBuilder.append(label.getText()).append("\n");
		}
		return stringBuilder.toString();
	}
}
