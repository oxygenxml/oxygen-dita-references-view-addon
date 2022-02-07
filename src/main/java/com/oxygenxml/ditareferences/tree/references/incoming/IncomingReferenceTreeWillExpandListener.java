package com.oxygenxml.ditareferences.tree.references.incoming;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A listener that manages the way the tree expands.
 * 
 * @author alex_smarandache
 *
 */
public class IncomingReferenceTreeWillExpandListener implements TreeWillExpandListener {	 
	
	/**
	 * Logger for logging.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(IncomingReferenceTreeWillExpandListener.class.getName());
	
	/**
	 * The tree where the listener is installed.
	 */
	private final IncomingReferencesTree tree;
	
	
	/**
	 * Constructor.
	 * 
	 * @param tree              The tree where the listener is installed.
	 * @param pluginWorkspace   The pluginworkspace.
	 */
	public IncomingReferenceTreeWillExpandListener(IncomingReferencesTree tree) {
		this.tree = tree;
	}
	
	
	/**
       * Add the children to current source node.
       *
       * @param source        The node source.
       * @param referenceInfo The current IncomingReference instance.
       *
       * @throws ClassNotFoundException
       * @throws InvocationTargetException
       * @throws NoSuchMethodException
       * @throws IllegalAccessException
       */
      private void addChildren(DefaultMutableTreeNode source, IncomingReference referenceInfo)
          throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException,
          IllegalAccessException, MalformedURLException {
        List<IncomingReference> temp;
        URL editorLocation = new URL(referenceInfo.getSystemId());
        temp = tree.searchIncomingRef(editorLocation);
        for (IncomingReference currentChild : temp) {
          DefaultMutableTreeNode currentChildNode = new DefaultMutableTreeNode(currentChild);
          source.add(currentChildNode);
        }

      }

      @Override
      public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
        DefaultMutableTreeNode source = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();

        if (source != null 
            && source.getChildCount() == 0 
            && source.getUserObject() instanceof IncomingReference) {
          expand(source);
        }
      }

      @Override
      public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
        // not needed
      }

      /**
       * 
       * @param source        The source node.
       * @param referenceInfo The current reference.
       * 
       * @return no of occurences for this reference.
       */
      private int getReferenceOccurences(DefaultMutableTreeNode source, IncomingReference referenceInfo) {
        TreeNode[] pathToRoot = ((DefaultTreeModel) tree.getModel()).getPathToRoot(source);
        int occurencesCounter = 0;
        String currentNodeSystemID = referenceInfo.getSystemId();

        for (TreeNode treeNode : pathToRoot) {
          DefaultMutableTreeNode nodeInPath = (DefaultMutableTreeNode) treeNode;
          if (nodeInPath.getUserObject() instanceof IncomingReference) {
            IncomingReference referenceInPath = (IncomingReference) nodeInPath.getUserObject();
            if (currentNodeSystemID != null && currentNodeSystemID.equals(referenceInPath.getSystemId())) {
              occurencesCounter++;
            }
          }
        }

        return occurencesCounter;
      }

      
      /**
       * Expands the source node.
       * 
       * @param source The source Node.
       */
      private void expand(DefaultMutableTreeNode source) {
        try {
          IncomingReference referenceInfo = (IncomingReference) (source.getUserObject());
          int occurencesCounter = getReferenceOccurences(source, referenceInfo);

          if (occurencesCounter < 2) {
            addChildren(source, referenceInfo);
          } else {
            // Avoid expanding the same system id on multiple levels in the same path
          }

         } catch (ClassNotFoundException 
            | NoSuchMethodException 
            | IllegalAccessException
            | InvocationTargetException 
            | MalformedURLException e1) {
          LOGGER.error(String.valueOf(e1), e1);
        }
      }
	
}