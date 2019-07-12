package com.oxygenxml.sdksamples.workspace;

import java.io.StringReader;

import javax.swing.JLabel;
import javax.swing.tree.TreePath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import junit.framework.TestCase;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextNodeRange;
import ro.sync.exml.workspace.api.editor.page.text.xml.XPathException;

public class ReferencesTreeTest extends TestCase {

	public void testTreeWhenNoDITATopic() {
		ReferencesTree tree = new ReferencesTree(new StandalonePluginWorkspaceAccessForTests());
		JLabel secondRowRenderLabel;

		// No file opened
		tree.refreshReferenceTree(null);
		TreePath path = tree.getPathForRow(0);
		JLabel firstRowRenderLabel = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree,
				path.getLastPathComponent(), true, true, true, 0, true);
		assertEquals("Outgoing references not available", firstRowRenderLabel.getText());

		// File opened but not in Text mode
		tree.refreshReferenceTree(new WSEditorAdapterForTests() {
			@Override
			public String getCurrentPageID() {
				return PAGE_GRID;
			}
		});
		path = tree.getPathForRow(0);
		firstRowRenderLabel = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree,
				path.getLastPathComponent(), true, true, true, 0, true);
		assertEquals("Outgoing references not available", firstRowRenderLabel.getText());

		// File opened in Text mode but it's not a XML file
		tree.refreshReferenceTree(new WSEditorAdapterForTests() {
			@Override
			public String getCurrentPageID() {
				return PAGE_TEXT;
			}
		});
		path = tree.getPathForRow(0);
		firstRowRenderLabel = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree,
				path.getLastPathComponent(), true, true, true, 0, true);
		assertEquals("Outgoing references not available", firstRowRenderLabel.getText());

		// XML File opened in Text mode but it's not a DITA file
		final String xmlContent = "<root/>";
		tree.refreshReferenceTree(new WSEditorAdapterForTests() {
			@Override
			public String getCurrentPageID() {
				return PAGE_TEXT;
			}

			@Override
			public WSEditorPage getCurrentPage() {
				return new WSXMLTextEditorPageForTests() {
					@Override
					public Object[] evaluateXPath(String xpathExpression) throws XPathException {
						return evaluateAllRefsExpression(xmlContent);
					}

					@Override
					public WSXMLTextNodeRange[] findElementsByXPath(String xpathExpression) throws XPathException {
						return new WSXMLTextNodeRange[] { new WSXMLTextNodeRangeForTests() };
					}
				};
			}
		});
		path = tree.getPathForRow(0);
		firstRowRenderLabel = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree,
				path.getLastPathComponent(), true, true, true, 0, true);
		assertEquals("Outgoing references not available", firstRowRenderLabel.getText());

		// DITA composite opened in Text mode but WITHOUT any reference inside it
		final String ditaCompNoRefContent = "<dita/>";
		tree.refreshReferenceTree(new WSEditorAdapterForTests() {
			@Override
			public String getCurrentPageID() {
				return PAGE_TEXT;
			}

			@Override
			public WSEditorPage getCurrentPage() {
				return new WSXMLTextEditorPageForTests() {
					@Override
					public Object[] evaluateXPath(String xpathExpression) throws XPathException {
						return evaluateAllRefsExpression(ditaCompNoRefContent);
					}

					@Override
					public WSXMLTextNodeRange[] findElementsByXPath(String xpathExpression) throws XPathException {
						return new WSXMLTextNodeRange[] { new WSXMLTextNodeRangeForTests() };
					}
				};
			}
		});
		path = tree.getPathForRow(0);
		firstRowRenderLabel = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree,
				path.getLastPathComponent(), true, true, true, 0, true);
		assertEquals("No outgoing references found", firstRowRenderLabel.getText());

		// DITA topic opened in Text mode but WITHOUT any reference inside it
		final String ditaTopicContent = "<gigel class=' topic/topic '/>";
		tree.refreshReferenceTree(new WSEditorAdapterForTests() {
			@Override
			public String getCurrentPageID() {
				return PAGE_TEXT;
			}

			@Override
			public WSEditorPage getCurrentPage() {
				return new WSXMLTextEditorPageForTests() {
					@Override
					public Object[] evaluateXPath(String xpathExpression) throws XPathException {
						return evaluateAllRefsExpression(ditaTopicContent);
					}

					@Override
					public WSXMLTextNodeRange[] findElementsByXPath(String xpathExpression) throws XPathException {
						return new WSXMLTextNodeRange[] { new WSXMLTextNodeRangeForTests() };
					}
				};
			}
		});
		path = tree.getPathForRow(0);
		firstRowRenderLabel = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree,
				path.getLastPathComponent(), true, true, true, 0, true);
		assertEquals("No outgoing references found", firstRowRenderLabel.getText());

		// DITA composite opened in Text mode WITH references inside of it
		final String ditaCompositeContent = "<dita>\n" + "    <topic id=\"topic_xst_3qn_j3b\" >\n"
				+ "        <title></title>\n" + "        <body>\n" + "            <p><xref class='- topic/xref '\n"
				+ "                href=\"https://docs.oracle.com/javase/7/docs/api/javax/swing/AbstractAction.html\"\n"
				+ "                format=\"html\" scope=\"external\"/></p>\n" + "        </body>\n" + "    </topic>\n"
				+ "</dita>";
		tree.refreshReferenceTree(new WSEditorAdapterForTests() {
			@Override
			public String getCurrentPageID() {
				return PAGE_TEXT;
			}

			@Override
			public WSEditorPage getCurrentPage() {
				return new WSXMLTextEditorPageForTests() {
					@Override
					public Object[] evaluateXPath(String xpathExpression) throws XPathException {
						return evaluateAllRefsExpression(ditaCompositeContent);
					}

					@Override
					public WSXMLTextNodeRange[] findElementsByXPath(String xpathExpression) throws XPathException {
						return new WSXMLTextNodeRange[] { new WSXMLTextNodeRangeForTests(),
								new WSXMLTextNodeRangeForTests() };
					}
				};
			}
		});
		path = tree.getPathForRow(0);
		secondRowRenderLabel = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree,
				path.getLastPathComponent(), true, true, true, 1, true);
		assertEquals("Cross references", secondRowRenderLabel.getText());

		// DITA topic opened in Text mode with 1 reference inside it
		final String ditaTopic1Content = "<task class='- topic/topic task/task'>" + "<title>Washing the car</title>\n"
				+ "<shortdesc>Keep your car looking great by washing it regularly.</shortdesc>\n" + "<taskbody>\n"
				+ "<result><p><image class='- topic/image '\n"
				+ "			href=\"../image/carwash.jpg\" alt=\"washing the car\" height=\"171\"\n"
				+ "width=\"249\"/></p></result>\n" + "</taskbody>\n" + "\n" + "</task>";
		tree.refreshReferenceTree(new WSEditorAdapterForTests() {
			@Override
			public String getCurrentPageID() {
				return PAGE_TEXT;
			}

			@Override
			public WSEditorPage getCurrentPage() {
				return new WSXMLTextEditorPageForTests() {
					@Override
					public Object[] evaluateXPath(String xpathExpression) throws XPathException {
						return evaluateAllRefsExpression(ditaTopic1Content);
					}

					@Override
					public WSXMLTextNodeRange[] findElementsByXPath(String xpathExpression) throws XPathException {
						return new WSXMLTextNodeRange[] { new WSXMLTextNodeRangeForTests(),
								new WSXMLTextNodeRangeForTests() };
					}
				};
			}
		});
		path = tree.getPathForRow(1);
		// System.err.println("PATH " + path);
		secondRowRenderLabel = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree,
				path.getLastPathComponent(), true, true, true, 2, true);
		assertEquals("../image/carwash.jpg", firstRowRenderLabel.getText());

		// DITA topic opened in Text mode with 2 different reference categories inside
		// of it
		final String ditaTopic2Content = "<task class='- topic/topic task/task'>"
				+ "<title>Changing the oil in your car</title>\n"
				+ "<shortdesc>Once every 6000 kilometers or three months, change the oil in\n"
				+ "your car.</shortdesc>\n" + "<taskbody>\n" + "<context><p>Changing the oil regularly \n"
				+ "will help keep the engine in good condition. </p></context>\n"
				+ "  <result><p><image class='- topic/image '\n"
				+ "				href=\"../image/mynextcarwash.jpg\" alt=\"washing the car\" height=\"171\"\n"
				+ "    width=\"249\"/></p></result>\n" + "</taskbody>\n" + "<related-links>\n"
				+ "<link class='- topic/link ' \n"
				+ "				href=\"../concepts/oil.dita\" format=\"dita\" type=\"concept\"></link>\n"
				+ "<link class='- topic/link ' \n"
				+ "			 	href=\"../concepts/wwfluid.dita\" format=\"dita\" type=\"concept\"></link>\n"
				+ "</related-links>\n" + "</task>";

		tree.refreshReferenceTree(new WSEditorAdapterForTests() {
			@Override
			public String getCurrentPageID() {
				return PAGE_TEXT;
			}

			@Override
			public WSEditorPage getCurrentPage() {
				return new WSXMLTextEditorPageForTests() {
					@Override
					public Object[] evaluateXPath(String xpathExpression) throws XPathException {
						return evaluateAllRefsExpression(ditaTopic2Content);
					}

					@Override
					public WSXMLTextNodeRange[] findElementsByXPath(String xpathExpression) throws XPathException {
						return new WSXMLTextNodeRange[] { new WSXMLTextNodeRangeForTests(),
								new WSXMLTextNodeRangeForTests(), new WSXMLTextNodeRangeForTests(),
								new WSXMLTextNodeRangeForTests() };
					}
				};
			}
		});
		path = tree.getPathForRow(1);
		System.err.println("PATH " + path);
		secondRowRenderLabel = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree,
				path.getLastPathComponent(), true, true, true, 2, true);
		assertEquals("... ge/mynextcarwash.jpg", firstRowRenderLabel.getText());

		path = tree.getPathForRow(3);
		System.err.println("PATH " + path);
		secondRowRenderLabel = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree,
				path.getLastPathComponent(), true, true, true, 4, true);
		assertEquals("../concepts/oil.dita", firstRowRenderLabel.getText());

	}

	/**
	 * Evaluate the xpath expression in case of a DITA topic.
	 * 
	 * @param ditaContent
	 * @return
	 */
	Object[] evaluateAllRefsExpression(final String ditaContent) {
		try {
			XPathExpression xpath = XPathFactory.newInstance().newXPath()
					.compile(ReferencesTree.ALL_REFS_XPATH_EXPRESSION);
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

}
