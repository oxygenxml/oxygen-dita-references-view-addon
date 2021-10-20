package com.oxygenxml.ditareferences.tree.references.incoming;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;

import com.oxygenxml.ditareferences.i18n.Tags;
import com.oxygenxml.ditareferences.workspace.StandalonePluginWorkspaceAccessForTests;

import junit.framework.TestCase;
import ro.sync.exml.workspace.api.PluginWorkspace;

/**
 * Test the com.oxygenxml.ditareferences.tree.references.incoming.IncomingReference class.
 * 
 * @author alex_smarandache
 *
 */
public class IncomingReferenceUtilTest extends TestCase {

	
	/**
	 * Test the method that returns the category for the given reference.
	 * <br><br>
	 * EXM-48855
	 * 
	 * @author alex_smarandache
	 * 
	 * @throws Exception
	 */
	public void testGetReferenceCategory() throws Exception {
		DPIForTest dpi = new DPIForTest();
		String systemID = "file:/C:/Users/alex_smarandache/Documents/GitHub/userguide-private/DITA/maps/chapter-installation.ditamap";
		dpi.setSystemID(systemID);
		dpi.setMessage("My message");
		
		assertEquals(ReferenceCategory.MAP, IncomingReferenceUtil.getReferenceCategory(dpi));
		
		systemID = "file:/C:/Users/alex_smarandache/Documents/GitHub/userguide-private/DITA/maps/chapter-installation.dita";
		dpi.setSystemID(systemID);
		assertEquals(ReferenceCategory.CROSS, IncomingReferenceUtil.getReferenceCategory(dpi));
		
		dpi.setMessage("bla bla message [CONKEYREF]");
		assertEquals(ReferenceCategory.CONTENT, IncomingReferenceUtil.getReferenceCategory(dpi));
	}
	
	
	/**
	 * Test copy location method.
	 * <br><br>
	 * EXM-48872
	 * 
	 * @author alex_smarandache
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("serial")
	public void testIncomingReferenceCopyLocation() throws Exception {
		DPIForTest dpi = new DPIForTest();
		String systemID = "file:/C:/Users/alex_smarandache/Documents/GitHub/userguide-private/DITA/maps/chapter-installation.ditamap";
		dpi.setSystemID(systemID);
		dpi.setMessage("My message");
		
		IncomingReference incomingReference = new IncomingReference(dpi);
		
		JTree tree = new JTree() {
			
			@Override
			public Object getLastSelectedPathComponent() {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode() {
					@Override
					public Object getUserObject() {
						return incomingReference;
					}
				};
				
				return node;
			}
			
		};
		
		IncomingReferenceUtil.copyFileLocationToClipboard(tree);
	    
		String expectedToolTipText = incomingReference.toString();
		
		String actualToolTipText = (String) Toolkit.getDefaultToolkit()
                .getSystemClipboard().getData(DataFlavor.stringFlavor); 
		
		assertEquals(expectedToolTipText, actualToolTipText);
	}
	
	
	/**
	 * Test the open file method.
	 * 
	 * @author alex_smarandache
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("serial")
	public void testOpenReferenceMethod() throws Exception {
		DPIForTest dpi = new DPIForTest();
		String systemID = "file:/C:/Users/alex_smarandache/Documents/GitHub/userguide-private/DITA/maps/chapter-installation.ditamap";
		dpi.setSystemID(systemID);
		dpi.setMessage("My message");
		
		IncomingReference incomingReference = new IncomingReference(dpi);
		
		boolean[] wasOpened = new boolean[1];
		wasOpened[0] = false;
		
		PluginWorkspace pluginWorkspace = new StandalonePluginWorkspaceAccessForTests() {
		
			@Override
			public boolean open(URL url) {
				wasOpened[0] = true;
				return false;
			}
			
		};
		
		JTree tree = new JTree() {
			
			@Override
			public Object getLastSelectedPathComponent() {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode() {
					@Override
					public Object getUserObject() {
						return incomingReference;
					}
				};
				
				return node;
			}
			
		};
		
		IncomingReferenceUtil.openFileAndSelectReference(pluginWorkspace, tree, null);
	    
		assertTrue(wasOpened[0]);
	}
	
	
	/**
	 * Test the method that initializes the root with the nodes in the first level.
	 * 
	 * @author alex_smarandache
	 * 
	 * @throws Exception
	 */
	public void testInitializeRoot() throws Exception {
		DPIForTest dpi = new DPIForTest();
		String systemID = "file:/C:/Users/alex_smarandache/Documents/GitHub/userguide-private/DITA/maps/chapter-installation.ditamap";
		dpi.setSystemID(systemID);
		dpi.setMessage("My message");
		
		List<IncomingReference> incomingReferences = new ArrayList<>();
		incomingReferences.add(new IncomingReference(dpi));
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(Tags.INCOMING_REFERENCES);
		
		IncomingReferenceUtil.addReferencesCategoriesToRoot(incomingReferences, root);
		
		assertEquals(1, root.getChildCount());
		
		dpi = new DPIForTest();
		systemID = "file:/C:/Users/alex_smarandache/Documents/GitHub/userguide-private/DITA/maps/chapter-installation.dita";
		dpi.setSystemID(systemID);
		dpi.setMessage("My message");
		incomingReferences.add(new IncomingReference(dpi));
		root = new DefaultMutableTreeNode(Tags.INCOMING_REFERENCES);
		
        IncomingReferenceUtil.addReferencesCategoriesToRoot(incomingReferences, root);
		
		assertEquals(2, root.getChildCount());
		
		dpi = new DPIForTest();
		systemID = "file:/C:/Users/alex_smarandache/Documents/GitHub/userguide-private/DITA/maps/chapter.dita";
		dpi.setSystemID(systemID);
		dpi.setMessage("My message");
		incomingReferences.add(new IncomingReference(dpi));
		root = new DefaultMutableTreeNode(Tags.INCOMING_REFERENCES);
		
        IncomingReferenceUtil.addReferencesCategoriesToRoot(incomingReferences, root);
		
		assertEquals(2, root.getChildCount());
		
		dpi = new DPIForTest();
		systemID = "file:/C:/Users/alex_smarandache/Documents/GitHub/userguide-private/DITA/maps/chapter.dita";
		dpi.setSystemID(systemID);
		dpi.setMessage("Oxygen [CONKEYREF]");
		incomingReferences.add(new IncomingReference(dpi));
		root = new DefaultMutableTreeNode(Tags.INCOMING_REFERENCES);
		
        IncomingReferenceUtil.addReferencesCategoriesToRoot(incomingReferences, root);
		
		assertEquals(3, root.getChildCount());
		
		int[] counter = new int[1];
		counter[0] = 0;
	
		visitAllNodes(root, counter);
	
		final int expectedNodesNumber = 8;
		
		assertEquals(expectedNodesNumber, counter[0]);
		
        JTree tree = new JTree();
        int[] expandedNodesCounter = new int[1];
        expandedNodesCounter[0] = 0;
        
        tree.addTreeWillExpandListener(new TreeWillExpandListener() {
			
			@Override
			public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
				expandedNodesCounter[0]++;
				
			}
			
			@Override
			public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
				// Not nedeed.
				
			}
		});
		
        DefaultTreeModel referencesTreeModel = new DefaultTreeModel(root) {
			@Override
			public boolean isLeaf(Object node) {
				return false;
			} // NOSONAR
		};

        tree.setModel(referencesTreeModel);
		IncomingReferenceUtil.expandFirstLevelOfTree(root, tree);
	
		// expands the reference categories nodes: MAP, CROSS, CONTENT
		final int expectedExpandedNodes = 3;
		
		assertEquals(expectedExpandedNodes, expandedNodesCounter[0]);
		
	}
	
	
	/**
	 * Visit all nodes from a node.
	 * 
	 * @param node     The current node.
	 * @param counter  Counter for all nodes.
	 * 
	 * @author alex_smarandache
	 *
	 */
	@SuppressWarnings("rawtypes")
	private static void visitAllNodes(TreeNode node, int[] counter) {
		counter[0]++;
	    if (node.getChildCount() >= 0) {
	      for (Enumeration e = node.children(); e.hasMoreElements();) {
	        TreeNode n = (TreeNode) e.nextElement();
	        visitAllNodes(n, counter);
	      }
	    }   
	 }
	
}
