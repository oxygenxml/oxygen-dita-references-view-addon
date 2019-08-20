package com.oxygenxml.ditareferences.workspace.rellinks;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RellinksAccessor {
	
	public static boolean forTests = false;

	public static List<RelLink> getRelationshipTableTargetURLs(URL topicURL) {
		List<RelLink> links = new ArrayList<RelLink>();
		forTests = false;

//		try {
//			Class ditaAccessClass = Class.forName("ro.sync.ecss.dita.DITAAccess" + (forTests ? "ForTests" : ""));
//			Method getRelLinks = ditaAccessClass.getDeclaredMethod("getRelatedLinksFromReltable", URL.class);
//			List allLinks = (List) getRelLinks.invoke(null, topicURL);
//			if (allLinks != null) {
//				int size = allLinks.size();
//				for (int i = 0; i < size; i++) {
//					Object relLink = allLinks.get(i);
//					Class relLinkClass = relLink.getClass();
//					Method getSource = relLinkClass.getMethod("getSourceURL");
//					URL sourceURL = (URL) getSource.invoke(relLink);
//					// if (topicURL.equals(sourceURL)) {
//					Method getTarget = relLinkClass.getMethod("getTargetURL");
//					URL targetURL = (URL) getTarget.invoke(relLink);
//
//					Method getTargetFormat = relLinkClass.getMethod("getTargetFormat");
//					String targetFormat = (String) getTargetFormat.invoke(relLink);
//
//					Method getTargetScope = relLinkClass.getMethod("getTargetScope");
//					String targetScope = (String) getTargetScope.invoke(relLink);
//
//					Method getTargetDefinitionLocation = relLinkClass.getMethod("getTargetDefinitionLocation");
//					URL targetDefLocationURL = (URL) getTargetDefinitionLocation.invoke(relLink);
//
//					RelLinkImpl relLinkImpl = new RelLinkImpl(sourceURL, targetURL, targetFormat, targetScope,
//							targetDefLocationURL);
//					links.add(relLinkImpl);
//					// }
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		return links;
	}
	
//	public static void main(String[] args) throws MalformedURLException {
//		RellinksAccessor.forTests = true;
//		List<RelLink> list = RellinksAccessor.getRelationshipTableTargetURLs(new File("test/source1.dita").toURI().toURL());
//		for (int i = 0; i < list.size(); i++) {
//			System.err.println(list.get(i).getTargetDefinitionLocation());
//		}
//	}
	
}
