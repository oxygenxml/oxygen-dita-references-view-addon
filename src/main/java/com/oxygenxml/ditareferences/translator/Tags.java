package com.oxygenxml.ditareferences.translator;

/**
 * Tags used for internationalization.
 * 
 * @author Alexandra_Dinisor
 *
 */
public class Tags {

	/**
	 * Private constructor.
	 */
	private Tags() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Label for the DITA outgoing references side view
	 */
	public static final String DITA_REFERENCES = "DITA_references";

	/**
	 * Text if the references are not available.
	 */
	public static final String OUTGOING_REFERENCES_NOT_AVAILABLE = "Outgoing_references_not_available";

	/**
	 * Text if the references are found.
	 */
	public static final String NO_OUTGOING_REFERENCES_FOUND = "No_outgoing_references_found";

	/**
	 * Hidden label for the root of the References Tree.
	 */
	public static final String ROOT_REFERENCES = "Root_references";

	/**
	 * Label for image references (direct and indirect).
	 */
	public static final String IMAGE_REFERENCES = "Image_references";

	/**
	 * Label for content references (conref and conkeyref).
	 */
	public static final String CONTENT_REFERENCES = "Content_references";

	/**
	 * Label for cross references (internal links).
	 */
	public static final String CROSS_REFERENCES = "Cross_references";

	/**
	 * Label for related links.
	 */
	public static final String RELATED_LINKS = "Related_links";

	/**
	 * Label for References PopUpMenu.
	 */
	public static final String OPEN_REFERENCE = "Open_reference";
	
}
