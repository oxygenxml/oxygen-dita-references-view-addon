/**
 * 
 */
package com.oxygenxml.ditareferences.workspace.text;

import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.tree.TreePath;

import org.junit.Test;

import com.oxygenxml.ditareferences.tree.references.outgoing.OutgoingReferencesTree;
import com.oxygenxml.ditareferences.workspace.StandalonePluginWorkspaceAccessForTests;
import com.oxygenxml.ditareferences.workspace.TestUtil;
import com.oxygenxml.ditareferences.workspace.WSEditorAdapterForTests;

import junit.framework.TestCase;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextNodeRange;
import ro.sync.exml.workspace.api.editor.page.text.xml.XPathException;

/**
 * Test different files for ReferencesTree nodes.
 * 
 * @author Alexandra_Dinisor
 *
 */
public class RefTreeRenderingForTextPageTest extends TestCase {

	final OutgoingReferencesTree tree = new OutgoingReferencesTree(new StandalonePluginWorkspaceAccessForTests(), null);

	final String ditaTopicAllRefsContent = "<topic id=\"sample\" class=\"- topic/topic \">\n"
			+ "    <title class=\"- topic/title \">sample</title>\n" + "    <body>\n"
			+ "            <image class=\"- topic/image \" href=\"image.png\"/>\n"
			+ "            <image class=\"- topic/image \" href=\"image\" format=\"png\"/> '\n "
			+ "            <p>Link to external resource <xref keyref=\"google\"/> </p>\n"
			+ "            <p class=\"- topic/p \" conref=\"sample2.dita#sample2/i1\"/>\n"
			+ "            <p class=\"- topic/p \" conkeyref=\"sample2/i1\" conrefend=\"bla.dita#test/i3\"/> \n"
			+ "            <object data=\"http://www.nasa.gov/mp3/590318main_ringtone_135_launch.mp3\" \n" 
			+ "                                    outputclass=\"audio\" class=\"- topic/object \" />  \n" 		
			+ "    </body>\n " + "    <related-links>\n"
			+ "            <link class=\"- topic/link \" href=\"www.google.com\" format=\"html\" scope=\"external\"/>\n"
			+ "            <link class=\"- topic/link \" href=\"sample2.dita\"/>\n"
			+ "            <link class=\"- topic/link \" href=\"test.pdf\" format=\"pdf\"><linktext>binary resource</linktext></link>\n"
			+ "            <link class=\"- topic/link \" keyref=\"google\"/>\n"
			+ "            <link class=\"- topic/link \" keyref=\"sample2\"/>\n"
			+ "            <link class=\"- topic/link \" keyref=\"myPDF\"><linktext>binary resource</linktext></link>\n"
			+ "    </related-links>\n" + "</topic>";

	/**
	 * Test the tree root when no file is opened.
	 */
	@Test
	public void test_NoFileOpened() {
		tree.setShowing(true);
		tree.refresh(null);
		TreePath path = tree.getPathForRow(0);
		JLabel firstRowRenderLabel = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree,
				path.getLastPathComponent(), true, true, true, 0, true);

		assertEquals("Outgoing_references_not_available", firstRowRenderLabel.getText());
	}

	/**
	 * File opened but not in Text Mode.
	 */
	@Test
	public void test_GridMode() {
		tree.setShowing(true);
		tree.refresh(new WSEditorAdapterForTests() {
			@Override
			public String getCurrentPageID() {
				return PAGE_GRID;
			}
		});

		TreePath path = tree.getPathForRow(0);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				true, true, true, 0, true);
		assertEquals("Outgoing_references_not_available", label.getText());
	}

	/**
	 * File opened in Text Mode but it's not a XML file.
	 */
	@Test
	public void test_NoXMLFileTextMode() {
		tree.setShowing(true);
		tree.refresh(new WSEditorAdapterForTests() {
			@Override
			public String getCurrentPageID() {
				return PAGE_TEXT;
			}
		});
		TreePath path = tree.getPathForRow(0);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				true, true, true, 0, true);
		assertEquals("Outgoing_references_not_available", label.getText());
	}

	/**
	 * XML File opened in Text mode but it's not a DITA file.
	 */
	@Test
	public void test_NoDITAFile() {
		final String noDITAContent = "<root/>";
		tree.setShowing(true);
		tree.refresh(createWSEditorAdapterForTextPage(noDITAContent, 1));
		TreePath path = tree.getPathForRow(0);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				true, true, true, 0, true);
		assertEquals("Outgoing_references_not_available", label.getText());
	}

	/**
	 * DITA Composite opened in Text Mode with no references inside.
	 */
	@Test
	public void test_NoRefsInDITAComposite() {
		final String ditaCompositeNoRefContent = "<dita/>";
		tree.setShowing(true);
		tree.refresh(createWSEditorAdapterForTextPage(ditaCompositeNoRefContent, 1));
		TreePath path = tree.getPathForRow(0);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				true, true, true, 0, true);
		assertEquals("No_outgoing_references_found", label.getText());

	}

	/**
	 * DITA Composite opened in Text Mode WITH references inside.
	 */
	@Test
	public void test_DITACompositeWithRefs() {
		String ditaCompositeContent = "<dita>\n" + "    <topic id=\"topic_xst_3qn_j3b\" >\n"
				+ "        <title></title>\n" + "        <body>\n"
				+ "            <p><xref class=\"- topic/xref \" href=\"https://docs.oracle.com/javase/7/docs/api/javax/swing/AbstractAction.html\"\n"
				+ "                format=\"html\" scope=\"external\"/></p>\n" + "        </body>\n" + "    </topic>\n"
				+ " </dita>";
		tree.setShowing(true);
		tree.refresh(createWSEditorAdapterForTextPage(ditaCompositeContent, 2));
		TreePath path = tree.getPathForRow(0);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				true, true, true, 0, true);
		assertEquals("Cross_references", label.getText());
	}

	/**
	 * DITA topic opened in Text Mode but WITHOUT any reference inside.
	 */
	@Test
	public void test_DITATopicNoRefs() {
		String ditaTopicNoRefsContent = "<gigel class=' topic/topic '/>";
		tree.setShowing(true);
		tree.refresh(createWSEditorAdapterForTextPage(ditaTopicNoRefsContent, 1));
		TreePath path = tree.getPathForRow(0);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				true, true, true, 0, true);
		assertEquals("No_outgoing_references_found", label.getText());
	}

	/**
	 * DITA topic opened in Text Mode with ALL reference categories inside.
	 * @throws InterruptedException 
	 */
	@Test
	public void test_DITAWithAllRefsInTextMode() throws InterruptedException {
		tree.setShowing(true);
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		tree.refresh(createWSEditorAdapterForTextPage(ditaTopicAllRefsContent, 15));
		
		TreePath path = tree.getPathForRow(1);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				true, true, true, 1, true);
		assertEquals("image.png", label.getText());

		path = tree.getPathForRow(8);
		
		label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(), true,
				true, true, 8, true);
		assertEquals("www.google.com", label.getToolTipText());

		path = tree.getPathForRow(9);
		label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(), true,
				true, true, 9, true);
		assertEquals("sample2.dita", label.getText());
	}

	/**
	 * Create the WSEditor for TextPage depending on the XPath expression.
	 * 
	 * @param content        The DITA Content
	 * @param noOfNodeRanges Number of NodeRanges
	 * @return WSEditor in TextMode with XPath evaluation
	 */
	private WSEditorAdapterForTests createWSEditorAdapterForTextPage(String content, int noOfNodeRanges) {
		return new WSEditorAdapterForTests() {
			@Override
			public String getCurrentPageID() {
				return PAGE_TEXT;
			}

			@Override
			public WSEditorPage getCurrentPage() {
				return new WSXMLTextEditorPageForTests() {
					@Override
					public Object[] evaluateXPath(String xpathExpression) throws XPathException {
						return TestUtil.evaluateAllRefsExpression(content);
					}

					@Override
					public WSXMLTextNodeRange[] findElementsByXPath(String xpathExpression) throws XPathException {
						return new WSXMLTextNodeRange[noOfNodeRanges];
					}
				};
			}
		};
	}

}
