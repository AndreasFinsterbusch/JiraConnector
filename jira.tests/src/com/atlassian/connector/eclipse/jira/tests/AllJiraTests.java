/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package me.glindholm.connector.eclipse.jira.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import me.glindholm.connector.eclipse.jira.tests.core.JiraClientFactoryServerUnrelatedTest;
import me.glindholm.connector.eclipse.jira.tests.core.JiraClientFactoryTest;
import me.glindholm.connector.eclipse.jira.tests.core.JiraCustomQueryTest;
import me.glindholm.connector.eclipse.jira.tests.core.JiraFilterTest;
import me.glindholm.connector.eclipse.jira.tests.core.JiraRepositoryConnectorTest;
import me.glindholm.connector.eclipse.jira.tests.core.JiraStackTraceDuplicateDetectorTest;
import me.glindholm.connector.eclipse.jira.tests.core.JiraTaskAttachmentHandlerTest;
import me.glindholm.connector.eclipse.jira.tests.core.JiraTaskDataHandlerTest;
import me.glindholm.connector.eclipse.jira.tests.core.JiraTaskExternalizationTest;
import me.glindholm.connector.eclipse.jira.tests.ui.JiraConnectorUiStandaloneTest;
import me.glindholm.connector.eclipse.jira.tests.ui.JiraConnectorUiTest;
import me.glindholm.connector.eclipse.jira.tests.util.JiraFixture;

/**
 * @author Wesley Coelho (initial integration patch)
 * @author Steffen Pingel
 */
public class AllJiraTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for me.glindholm.connector.eclipse.jira.tests");
		suite.addTest(AllJiraHeadlessStandaloneTests.suite());
		suite.addTestSuite(JiraConnectorUiStandaloneTest.class);
		suite.addTestSuite(JiraClientFactoryServerUnrelatedTest.class);

		// repository tests
		for (JiraFixture fixture : new JiraFixture[] { JiraFixture.DEFAULT }) {
			fixture.createSuite(suite);
			fixture.add(JiraCustomQueryTest.class);
			fixture.add(JiraClientFactoryTest.class);
			fixture.add(JiraTaskExternalizationTest.class);
			fixture.add(JiraRepositoryConnectorTest.class);
			fixture.add(JiraTaskAttachmentHandlerTest.class);
			fixture.add(JiraTaskDataHandlerTest.class);
			fixture.add(JiraStackTraceDuplicateDetectorTest.class);
			fixture.add(JiraConnectorUiTest.class);
			fixture.add(JiraFilterTest.class);
			fixture.done();
		}
		return suite;
	}
}
