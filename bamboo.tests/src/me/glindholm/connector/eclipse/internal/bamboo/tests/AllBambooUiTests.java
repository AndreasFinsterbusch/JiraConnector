/**
 * 
 */
package me.glindholm.connector.eclipse.internal.bamboo.tests;

import me.glindholm.connector.eclipse.internal.bamboo.ui.BambooBuildViewerComparatorTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * All of the Bamboo tests for the JiraConnector for Eclipse can be run from this class
 * 
 * @author Thomas Ehrnhoefer
 * 
 */
public class AllBambooUiTests {

	private AllBambooUiTests() {
	}

	public static Test suite() {

		TestSuite suite = new TestSuite("Tests for Bamboo");
		// $JUnit-BEGIN$
		suite.addTestSuite(BambooBuildViewerComparatorTest.class);

		// $JUnit-END$
		return suite;
	}

}
