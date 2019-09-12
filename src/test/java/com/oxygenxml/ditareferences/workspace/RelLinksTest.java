package com.oxygenxml.ditareferences.workspace;

import java.awt.Rectangle;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.oxygenxml.ditareferences.workspace.rellinks.RelLinksAccessor;
import com.oxygenxml.ditareferences.workspace.text.WSXMLTextEditorPageForTests;

import junit.framework.TestCase;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextNodeRange;
import ro.sync.exml.workspace.api.editor.page.text.xml.XPathException;
import ro.sync.exml.workspace.api.util.UtilAccess;
import ro.sync.util.URLUtil;

/**
 * Test for tree rendering with relLinks and double click for opening the
 * relLink.
 * 
 * @Alexandra_Dinisor
 *
 */
public class RelLinksTest extends TestCase{

	private static final Logger LOGGER = Logger.getLogger(RelLinksTest.class);
	
	final String ditaTopic = "<topic id=\"sample\" class=\"- topic/topic \">\n"
			+ "    <title class=\"- topic/title \">sample</title>\n"
			+ "    <body>\n"           
			+ "    </body>\n "
			+ "</topic>";
	
	private final WSEditorAdapterForTests editorAccess = new WSEditorAdapterForTests() {

		private WSXMLTextEditorPageForTests currentTextPage;

		@Override
		public String getCurrentPageID() {
			return PAGE_TEXT;
		}

		@Override
		public WSEditorPage getCurrentPage() {
			if (currentTextPage == null) {
				currentTextPage = new WSXMLTextEditorPageForTests() {
					@Override
					public Object[] evaluateXPath(String xpathExpression) throws XPathException {
						return TestUtil.evaluateAllRefsExpression(ditaTopic);
					}

					@Override
					public WSXMLTextNodeRange[] findElementsByXPath(String xpathExpression) throws XPathException {
						return new WSXMLTextNodeRange[5];
					}

					@Override
					public WSEditor getParentEditor() {
						return new WSEditorAdapterForTests() {
							@Override
							public URL getEditorLocation() {
								URL editorLocationUrl = null;
								try {
									// set the source File to match the method
									// ro.sync.ecss.dita.DITAAccessForTests.getRelatedLinksFromReltable(URL).
									editorLocationUrl = new File("test/source1.dita").toURI().toURL();
								} catch (MalformedURLException e) {
									LOGGER.debug(e, e);
								}
								return editorLocationUrl;

							};
						};
					}
				};
			}
			return currentTextPage;
		}

		@Override
		public URL getEditorLocation() {
			return URLUtil.convertToURL("file://C:/editor_location.ext");
		}

	};

	private final ArrayList<URL> urlRelLink = new ArrayList<URL>();
	private final ArrayList<URL> urlRelLink2 = new ArrayList<URL>();
	private final ArrayList<URL> urlToExternal = new ArrayList<URL>();

	final StandalonePluginWorkspaceAccessForTests pluginWorkspaceAccess = new StandalonePluginWorkspaceAccessForTests() {

		@Override
		public boolean open(URL url, String imposedPage, String imposedContentType) {
			urlRelLink.add(url);
			return super.open(url, imposedPage, imposedContentType);
		}

		@Override
		public boolean open(URL url) {
			urlRelLink2.add(url);
			return super.open(url);
		};

		@Override
		public void openInExternalApplication(URL url, boolean preferAssociatedApplication) {
			urlToExternal.add(url);
			super.openInExternalApplication(url, preferAssociatedApplication);
		}

		@Override
		public UtilAccess getUtilAccess() {
			return new UtilAccessForTests();

		}
	};

	ReferencesTree tree = new ReferencesTree(pluginWorkspaceAccess, null, new DITAReferencesTranslatorForTests());

	@Test
	public void test_RenderingRelLink() {
		tree.setShowing(true);
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		RelLinksAccessor.forTests = true;
		tree.refreshReferenceTree(editorAccess);

		assertEquals(3, tree.getRowCount());

		TreePath path = tree.getPathForRow(1);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				true, true, true, 1, true);
		assertTrue("Should have worked " + String.valueOf(label.getText()),
				label.getText().endsWith("test/target1.dita"));

		path = tree.getPathForRow(2);
		label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(), true,
				true, true, 2, true);
		assertEquals("http://www.google.com", label.getText());

	}

	@Test
	public void test_OpenExternalRelLink() {
		tree.setShowing(true);
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		RelLinksAccessor.forTests = true;
		tree.refreshReferenceTree(editorAccess);

		tree.setSelectionRow(2);
		TestUtil.simulateDoubleClick(tree);
		assertTrue("Should have worked " + String.valueOf(urlToExternal.get(0)),
				urlToExternal.get(0).toString().endsWith("google.com"));

	}

}
