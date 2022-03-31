/**
 * Copyright (C) 2008 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.glindholm.theplugin.bamboo.api.bamboomock;

import org.ddsteps.mock.httpserver.JettyMockServer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @autrhor pmaruszak
 * @date 12/10/10
 */
public class JobKeyForChainCallback implements JettyMockServer.Callback {
    private final String filePath;
    private final String planName;

    public JobKeyForChainCallback(final String filePath, final String planName) {
        this.filePath = filePath;
        this.planName = planName;
    }


    public void onExpectedRequest(String target, HttpServletRequest request, HttpServletResponse response) throws Exception {
        assertTrue(request.getPathInfo().endsWith("/rest/api/latest/plan/" + planName));

            final String[] expands = request.getParameterValues("expand");
            assertEquals(1, expands.length);

            final String expand = expands[0];
            assertEquals("stages.stage.plans", expand);

            Util.copyResourceWithFullPath(response.getOutputStream(), filePath);
            response.getOutputStream().flush();
    }
}