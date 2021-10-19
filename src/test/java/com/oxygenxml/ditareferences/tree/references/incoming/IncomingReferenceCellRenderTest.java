package com.oxygenxml.ditareferences.tree.references.incoming;

import javax.swing.JLabel;
import javax.swing.tree.DefaultMutableTreeNode;

import com.oxygenxml.ditareferences.workspace.StandalonePluginWorkspaceAccessForTests;

import junit.framework.TestCase;

/**
 * Used for test the /oxygen-dita-references-view/src/main/java/com/oxygenxml/ditareferences/tree/references/incoming/IncomingReferencesTreeCellRenderer class.
 * Test the cell render for incoming references and references categories.
 * 
 * @author alex_smarandache
 *
 */
public class IncomingReferenceCellRenderTest extends TestCase {
	
	/**
	 * Tests CellRender for References Categories.
	 * <br><br>
	 * EXM-48855
	 * 
	 * @author alex_smarandache
	 * 
	 * @throws Exception
	 */
	public void testIRCellRenderForReferencesCategories() throws Exception {
		IncomingReferencesTree tree = new IncomingReferencesTree(new StandalonePluginWorkspaceAccessForTests());
		IncomingReferencesTreeCellRenderer cellRender = (IncomingReferencesTreeCellRenderer) tree.getCellRenderer();
		
		JLabel label = (JLabel)cellRender.getTreeCellRendererComponent(tree, ReferenceCategory.MAP, true, 
				true, false, 1, true);
		assertEquals("MAP", label.getText());
		assertNull(label.getToolTipText());
		
		label = (JLabel)cellRender.getTreeCellRendererComponent(tree, ReferenceCategory.CONTENT, true, 
				true, false, 1, true);
		assertEquals("CONTENT", label.getText());
		assertNull(label.getToolTipText());
		
		label = (JLabel)cellRender.getTreeCellRendererComponent(tree, ReferenceCategory.CROSS, true, 
				true, false, 1, true);
		assertEquals("CROSS", label.getText());
		assertNull(label.getToolTipText());
	}
	
	
	/**
	 * Tests CellRender for incoming references.
	 * 
	 * @author alex_smarandache
	 * 
	 * @throws Exception
	 */
	public void testIRCellRenderForIncomingReferences() throws Exception {
		DPIForTest dpi = new DPIForTest();
		String systemID = "file:/C:/Users/alex_smarandache/Documents/GitHub/userguide-private/DITA/maps/chapter-installation.ditamap";
		dpi.setSystemID(systemID);
		dpi.setMessage("My message");
		
		IncomingReference incomingReference = new IncomingReference(dpi);
		
		IncomingReferencesTree tree = new IncomingReferencesTree(new StandalonePluginWorkspaceAccessForTests());
		IncomingReferencesTreeCellRenderer cellRender = (IncomingReferencesTreeCellRenderer) tree.getCellRenderer();
		
		JLabel label = (JLabel)cellRender.getTreeCellRendererComponent(tree, new DefaultMutableTreeNode(incomingReference), true, 
				true, false, 1, true);		
		assertEquals(incomingReference.getRenderText(), label.getText());
		assertEquals(incomingReference.getTooltipText(), label.getToolTipText());
	}
	
}
