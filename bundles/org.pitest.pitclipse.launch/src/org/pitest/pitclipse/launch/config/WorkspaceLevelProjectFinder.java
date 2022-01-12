/*******************************************************************************
 * Copyright 2012-2019 Phil Glover and contributors
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package org.pitest.pitclipse.launch.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;

import java.util.List;

import static org.pitest.pitclipse.launch.config.ProjectUtils.getOpenJavaProjects;
import static org.pitest.pitclipse.launch.config.ProjectUtils.onClassPathOf;
import static org.pitest.pitclipse.launch.config.ProjectUtils.sameProject;

public class WorkspaceLevelProjectFinder implements ProjectFinder {
    @Override
    public List<String> getProjects(LaunchConfigurationWrapper configurationWrapper) throws CoreException {
        Builder<String> results = ImmutableList.builder();
        IJavaProject testProject = configurationWrapper.getProject();
        List<IJavaProject> projects = getOpenJavaProjects();
        for (IJavaProject project : projects) {
            if (sameProject(testProject, project) || onClassPathOf(testProject, project)) {
                results.add(project.getProject().getName());
            }
        }
        return results.build();
    }
}
