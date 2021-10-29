package com.oxygenxml.ditareferences.tree.references.incoming;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.oxygenxml.ditareferences.i18n.Tags;
import com.oxygenxml.ditareferences.workspace.StandalonePluginWorkspaceAccessForTests;

import junit.framework.TestCase;

/**
 * Test com.oxygenxml.ditareferences.tree.references.incoming.IncomingReferencesTree class for different situations.
 * 
 * @author alex_smarandache
 *
 */
public class IncomingReferenceTreeTest extends TestCase {
	
	/**
	 * Test reset tree.
	 * 
	 * @author alex_smarandache
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("serial")
	public void testResetedTree() throws Exception {
		
		IncomingReferencesTree incomingReferencesTree = new IncomingReferencesTree(new StandalonePluginWorkspaceAccessForTests()) {
			
		    @Override
			List<IncomingReference> searchIncomingRef(URL editorLocation)
					throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		    	return null;
		    }
		    
		    @Override
		    public boolean isShowing() {
		    	return true;
		    }
		    
		    @Override
		    synchronized void refresh(URL editorLocation, ProgressStatusListener progressStatus, boolean clearCache) {
		    	super.refresh(null, 
				new ProgressStatusListener() {
					
					@Override
					public void updateInProgressStatus(boolean inProgress, int delay) {
						// noting
					}	
				}, 
				true
		       );
		    }
		};
		
		incomingReferencesTree.reset();
		incomingReferencesTree.refresh(null, null, false);
		TimeUnit.MILLISECONDS.sleep(50);
		assertEquals(1, ((DefaultMutableTreeNode)incomingReferencesTree.getModel().getRoot()).getChildCount());
		assertEquals(Tags.INCOMING_REFERENCES_NOT_AVAILABLE, 
				((DefaultMutableTreeNode)((DefaultMutableTreeNode)incomingReferencesTree.getModel().getRoot()).getChildAt(0)).getUserObject());
	}
	
	
	/**
	 * Test tree when were no references found.
	 * 
	 * @author alex_smarandache
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("serial")
	public void testNoIncomingReferencesFound() throws Exception {
		
		IncomingReferencesTree incomingReferencesTree = new IncomingReferencesTree(new StandalonePluginWorkspaceAccessForTests()) {
			
		    @Override
			List<IncomingReference> searchIncomingRef(URL editorLocation)
					throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		    	return new ArrayList<>();
		    }
		    
		    @Override
		    public boolean isShowing() {
		    	return true;
		    }
		    
		    @Override
		    synchronized void refresh(URL editorLocation, ProgressStatusListener progressStatus, boolean clearCache) {
		    	super.refresh(null, 
				new ProgressStatusListener() {
					
					@Override
					public void updateInProgressStatus(boolean inProgress, int delay) {
						// noting
					}	
				}, 
				true
		       );
		    }
		};
		
		incomingReferencesTree.refresh(null, null, true);
		
		// The model is set on AWT, a sleep is required to update the data.
		TimeUnit.MILLISECONDS.sleep(100);
		SwingUtilities.invokeLater(() -> {
			assertEquals(1, ((DefaultMutableTreeNode)incomingReferencesTree.getModel().getRoot()).getChildCount());
			DefaultMutableTreeNode root = (DefaultMutableTreeNode)incomingReferencesTree.getModel().getRoot();
			DefaultMutableTreeNode child = (DefaultMutableTreeNode)root.getChildAt(0);
			assertEquals(Tags.NO_INCOMING_REFERENCES_FOUND, child.getUserObject());
			assertEquals(0, child.getChildCount());
		});
		
	}
	
	
	/**
	 * Test the situation when there is a circular dependence of the form: a-> b -> c ->  ...-> a.
	 * <br><br>
	 * EXM-48561
	 * 
	 * @author alex_smarandache
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("serial")
	public void testForCircularDependences() throws Exception {
		
		List<IncomingReference> incomingReferences = createIncomingReferences();
		int[] indexCounter = new int[1];
		
		
		IncomingReferencesTree incomingReferencesTree = new IncomingReferencesTree(new StandalonePluginWorkspaceAccessForTests()) {
			
		    @Override
			List<IncomingReference> searchIncomingRef(URL editorLocation)
					throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		    	List<IncomingReference> references = new ArrayList<>();
		    	if(indexCounter[0] < incomingReferences.size()) {
		    		references.add(incomingReferences.get(indexCounter[0]));
		    	}
		    	
		    	// because for adding a node are two calls, first to check if the node are expandable and second to expand node
		    	indexCounter[0]++;
		        
		    	return references;
		    }
		    
		    @Override
		    public boolean isShowing() {
		    	return true;
		    }
		    
		    @Override
		    synchronized void refresh(URL editorLocation, ProgressStatusListener progressStatus, boolean clearCache) {
		    	super.refresh(null, 
				new ProgressStatusListener() {
					
					@Override
					public void updateInProgressStatus(boolean inProgress, int delay) {
						// noting
					}	
				}, 
				true
		       );
		    }
		};
		
		incomingReferencesTree.refresh(null, null, true);
		
		// The model is set on AWT, a sleep is required to update the data.
		TimeUnit.MILLISECONDS.sleep(300);
		
		assertEquals(1, ((DefaultMutableTreeNode)incomingReferencesTree.getModel().getRoot()).getChildCount());
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)incomingReferencesTree.getModel().getRoot();
		DefaultMutableTreeNode prevChild = (DefaultMutableTreeNode)root.getChildAt(0);
		assertEquals(ReferenceCategory.MAP, prevChild.getUserObject());
		assertEquals(1, prevChild.getChildCount());
		
		for(int i = 0; i < 5; i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode)prevChild.getChildAt(0);
			assertEquals(incomingReferences.get(indexCounter[0] - 1), child.getUserObject());
			incomingReferencesTree.expandPath(new TreePath(child.getPath()));
			assertEquals(1, child.getChildCount());
			prevChild = child;
		}
		
		DefaultMutableTreeNode child = (DefaultMutableTreeNode)prevChild.getChildAt(0);
		assertEquals(incomingReferences.get(indexCounter[0] - 1), child.getUserObject());
		incomingReferencesTree.expandPath(new TreePath(child.getPath()));
		assertEquals(0, child.getChildCount());	
	}
	
	
	/**
	 * Test the situation when there is a circular dependence of the form: a-> b -> ...-> a.
	 * <br><br>
	 * EXM-48561
	 * 
	 * @author alex_smarandache
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("serial")
	public void testSimpleExpansion() throws Exception {
		
		List<IncomingReference> incomingReferences = createIncomingReferences();
		int[] indexCounter = new int[1];
		
		
		IncomingReferencesTree incomingReferencesTree = new IncomingReferencesTree(new StandalonePluginWorkspaceAccessForTests()) {
			
			/**
			 * Count no calls for searchIncomingRef(URL editorLocation).
			 */
			private int noCalls = 0;
			
		    @Override
			List<IncomingReference> searchIncomingRef(URL editorLocation)
					throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		    	List<IncomingReference> references = new ArrayList<>();
		    	// expands first 5 nodes 
		    	if(noCalls == 0) {
		    		for(int i = 0; i < 5; i++) {
		    			references.add(incomingReferences.get(indexCounter[0]));
		    			indexCounter[0]++;
		    		}
		    	} else {
		    		if(indexCounter[0] < incomingReferences.size()) {
			    		references.add(incomingReferences.get(indexCounter[0]));
			    		indexCounter[0]++; 
			    	}
		    	}
		    	
		        
		        noCalls++;
		        
		    	return references;
		    }
		    
		    @Override
		    public boolean isShowing() {
		    	return true;
		    }
		    
		    @Override
		    synchronized void refresh(URL editorLocation, ProgressStatusListener progressStatus, boolean clearCache) {
		    	super.refresh(null, 
				new ProgressStatusListener() {
					
					@Override
					public void updateInProgressStatus(boolean inProgress, int delay) {
						// noting
					}	
				}, 
				true
		       );
		    }
		};
		
		incomingReferencesTree.refresh(null, null, true);
		
		// The model is set on AWT, a sleep is required to update the data.
		TimeUnit.MILLISECONDS.sleep(500);
		
		assertEquals(3, ((DefaultMutableTreeNode)incomingReferencesTree.getModel().getRoot()).getChildCount());
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)incomingReferencesTree.getModel().getRoot();
		
		DefaultMutableTreeNode mapChild = (DefaultMutableTreeNode)root.getChildAt(0);
		assertEquals(ReferenceCategory.MAP, mapChild.getUserObject());
		assertEquals(1, mapChild.getChildCount());
		
		DefaultMutableTreeNode crossChild = (DefaultMutableTreeNode)root.getChildAt(1);
		assertEquals(ReferenceCategory.CROSS, crossChild.getUserObject());
		assertEquals(1, crossChild.getChildCount());
		
		DefaultMutableTreeNode contentChild = (DefaultMutableTreeNode)root.getChildAt(2);
		assertEquals(ReferenceCategory.CONTENT, contentChild.getUserObject());
		assertEquals(3, contentChild.getChildCount());
		
		DefaultMutableTreeNode child = (DefaultMutableTreeNode)mapChild.getChildAt(0);
		assertEquals(incomingReferences.get(0), child.getUserObject());
		incomingReferencesTree.expandPath(new TreePath(child.getPath()));
		assertEquals(1, child.getChildCount());
		
		mapChild = (DefaultMutableTreeNode)child.getChildAt(0);
		assertEquals(incomingReferences.get(indexCounter[0] - 1), mapChild.getUserObject());
		incomingReferencesTree.expandPath(new TreePath(mapChild.getPath()));
		assertEquals(0, mapChild.getChildCount());
		
		child = (DefaultMutableTreeNode)crossChild.getChildAt(0);
		assertEquals(incomingReferences.get(1), child.getUserObject());
		incomingReferencesTree.expandPath(new TreePath(child.getPath()));
		assertEquals(1, child.getChildCount());
		
		crossChild = (DefaultMutableTreeNode)child.getChildAt(0);
		assertEquals(incomingReferences.get(indexCounter[0] - 1), crossChild.getUserObject());
		incomingReferencesTree.expandPath(new TreePath(crossChild.getPath()));
		assertEquals(1, crossChild.getChildCount());
		
		child = (DefaultMutableTreeNode)crossChild.getChildAt(0);
		assertEquals(incomingReferences.get(indexCounter[0] - 1), child.getUserObject());
		incomingReferencesTree.expandPath(new TreePath(child.getPath()));
		assertEquals(1, child.getChildCount());
		
		crossChild = (DefaultMutableTreeNode)child.getChildAt(0);
		assertEquals(incomingReferences.get(indexCounter[0] - 1), crossChild.getUserObject());
		assertEquals(0, crossChild.getChildCount());
		
		child = (DefaultMutableTreeNode)contentChild.getChildAt(0);
		assertEquals(incomingReferences.get(2), child.getUserObject());
		assertEquals(0, child.getChildCount());
		
		child = (DefaultMutableTreeNode)contentChild.getChildAt(1);
		assertEquals(incomingReferences.get(3), child.getUserObject());
		assertEquals(0, child.getChildCount());
		
		child = (DefaultMutableTreeNode)contentChild.getChildAt(2);
		assertEquals(incomingReferences.get(4), child.getUserObject());
		incomingReferencesTree.expandPath(new TreePath(child.getPath()));
		assertEquals(1, child.getChildCount());
		
		contentChild = (DefaultMutableTreeNode)child.getChildAt(0);
		assertEquals(incomingReferences.get(indexCounter[0] - 1), contentChild.getUserObject());
		incomingReferencesTree.expandPath(new TreePath(contentChild.getPath()));
		assertEquals(0, contentChild.getChildCount());
	}
	
	
	
	/**
     * Create a list with incoming references.
     *
	 * @return Created list.
	 */
	private List<IncomingReference> createIncomingReferences() {
		 List<IncomingReference> incomingReferences = new ArrayList<>();
		 
		 final int noReferences = 10;
		 
		 String[] systemID = {
				 "file:/C:/Users/alex_smarandache/Documents/GitHub/userguide-private/DITA/maps/chapter-installation.ditamap",
				 "file:/C:/Users/alex_smarandache/Documents/GitHub/userguide-private/DITA/maps/chapter-installation.dita",
				 "file:/C:/Users/alex_smarandache/Documents/GitHub/userguide-private/DITA/maps/chapte.dita",
				 "file:/C:/Users/alex_smarandache/Documents/GitHub/userguide-private/DITA/maps/chapter-installation1.ditamap",
				 "file:/C:/Users/alex_smarandache/Documents/GitHub/userguide-private/DITA/maps/chapter10-installation.ditamap",
				 "file:/C:/Users/alex_smarandache/Documents/GitHub/userguide-private/DITA/maps/chapter-installation.ditamap",
				 "file:/C:/Users/alex_smarandache/Documents/GitHub/userguide-private/DITA/maps/chapter2.dita",
				 "file:/C:/Users/alex_smarandache/Documents/GitHub/userguide-private/DITA/maps/chapter3.dita",
				 "file:/C:/Users/alex_smarandache/Documents/GitHub/userguide-private/DITA/maps/chapter3.ditamap",
				 "file:/C:/Users/alex_smarandache/Documents/GitHub/userguide-private/DITA/maps/chapter4.dita"
		 };
		 
		 String[] messages = {
				 "My custom message 1",
				 "My custom message 2",
				 "My key ref [CONKEYREF]",
				 "My key1 ref [CONKEYREF]",
				 "My key2 ref [CONKEYREF]",
				 "My key3 ref [CONKEYREF]",
				 "My key1 ref",
				 "My key4 ref [CONKEYREF]",
				 "My key09",
				 "My kekl",
		 };
		 
		 for(int i = 0; i < noReferences; i++) {
			 DPIForTest dpi = new DPIForTest();
			 dpi.setSystemID(systemID[i]);
			 dpi.setMessage(messages[i]);
			 incomingReferences.add(new IncomingReference(dpi));
		 }
			
		 return incomingReferences;
	}

}
