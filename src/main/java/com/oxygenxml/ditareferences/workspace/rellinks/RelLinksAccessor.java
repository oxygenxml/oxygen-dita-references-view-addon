package com.oxygenxml.ditareferences.workspace.rellinks;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class RelLinksAccessor {
	private static final Logger LOGGER = Logger.getLogger(RelLinksAccessor.class);

	private RelLinksAccessor() {
		// private constructor
	}

	public static boolean forTests = false;

	/**
	 * Get RelationshipTable Target URLs using reflection.
	 * 
	 * @param topicURL
	 * @return A list of Relationship Links from RelTable
	 */
	public static List<RelLink> getRelationshipTableTargetURLs(URL topicURL) {
		List<RelLink> links = new ArrayList<>();

		try {
			Class<?> ditaAccessClass = Class.forName("ro.sync.ecss.dita.DITAAccess" + (forTests ? "ForTests" : ""));
			Method getRelLinks = ditaAccessClass.getDeclaredMethod("getRelatedLinksFromReltable", URL.class);
			List<?> allLinks = (List<?>) getRelLinks.invoke(null, topicURL);

			if (allLinks != null) {
				int size = allLinks.size();
				for (int i = 0; i < size; i++) {
					Object relLink = allLinks.get(i);
					Class<? extends Object> relLinkClass = relLink.getClass();
					Method getSource = relLinkClass.getMethod("getSourceURL");
					URL sourceURL = (URL) getSource.invoke(relLink);
					
					String trimmedTopicURL = getURLWithoutAnchor(topicURL.toString());
					String trimmedSourceURL = getURLWithoutAnchor(sourceURL.toString());
															
					if (trimmedTopicURL.equals(trimmedSourceURL)) {
						Method getTarget = relLinkClass.getMethod("getTargetURL");
						URL targetURL = (URL) getTarget.invoke(relLink);

						Method getTargetFormat = relLinkClass.getMethod("getTargetFormat");
						String targetFormat = (String) getTargetFormat.invoke(relLink);

						Method getTargetScope = relLinkClass.getMethod("getTargetScope");
						String targetScope = (String) getTargetScope.invoke(relLink);

						Method getTargetDefinitionLocation = relLinkClass.getMethod("getTargetDefinitionLocation");
						URL targetDefLocationURL = (URL) getTargetDefinitionLocation.invoke(relLink);

						RelLinkImpl relLinkImpl = new RelLinkImpl(sourceURL, targetURL, targetFormat, targetScope,
								targetDefLocationURL);
						links.add(relLinkImpl);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.debug(e, e);
		}

		return links;
	}

	/**
	 * Get the Topic URL String without its anchor if any or the whole URL if no
	 * anchor found.
	 * 
	 * @param topicURLString The created Topic URL String
	 * @return The Topic URL without the anchor if any.
	 */
	private static String getURLWithoutAnchor(String topicURLString) {
		int indexOfAnchor = topicURLString.indexOf('#');
		if(indexOfAnchor != -1) {
			return topicURLString.substring(0, indexOfAnchor);
		}
		return topicURLString;
	}

}
