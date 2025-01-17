/**
 *
 */
package me.glindholm.connector.eclipse.internal.bamboo.tests;

import me.glindholm.connector.eclipse.internal.bamboo.core.BambooClientManagerTest;
import me.glindholm.connector.eclipse.internal.bamboo.core.BambooRepositoryConnectorTest;
import me.glindholm.connector.eclipse.internal.bamboo.core.BuildPlanManagerTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * All of the Bamboo tests for the JiraConnector for Eclipse can be run from this class
 *
 * @author Thomas Ehrnhoefer
 */
public class AllBambooCoreTests {
	private AllBambooCoreTests() {
	}

	public static Test suite() {

		TestSuite suite = new TestSuite("Tests for Bamboo");
		// $JUnit-BEGIN$
		suite.addTestSuite(BambooClientManagerTest.class);
		suite.addTestSuite(BambooRepositoryConnectorTest.class);
		suite.addTestSuite(BuildPlanManagerTest.class);
		// $JUnit-END$
		return suite;
	}
}
