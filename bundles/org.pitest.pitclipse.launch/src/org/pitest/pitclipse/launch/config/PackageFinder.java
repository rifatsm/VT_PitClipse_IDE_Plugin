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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.ImmutableList.copyOf;
import static org.eclipse.core.resources.IResource.FOLDER;
import static org.eclipse.core.resources.IResource.NONE;
import static org.eclipse.core.resources.IResource.PROJECT;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT_ROOT;
import static org.eclipse.jdt.core.IPackageFragment.DEFAULT_PACKAGE_NAME;

public class PackageFinder {

    private static final class ProjectLevelProxyVisitor implements IResourceProxyVisitor {
        private final LaunchConfigurationWrapper configurationWrapper;
        private final Builder<String> builder;

        private ProjectLevelProxyVisitor(LaunchConfigurationWrapper configurationWrapper, Builder<String> builder) {
            this.configurationWrapper = configurationWrapper;
            this.builder = builder;
        }

        @Override
        public boolean visit(IResourceProxy proxy) throws CoreException {
            IJavaProject project = configurationWrapper.getProject();
            if (proxy.getType() == FOLDER) {
                IJavaElement element = JavaCore.create(proxy.requestResource());
                if (element.getElementType() == PACKAGE_FRAGMENT) {
                    builder.add(element.getElementName() + ".*");
                } else if (element.getElementType() == PACKAGE_FRAGMENT_ROOT) {
                    builder.addAll(getPackagesFromRoot(project, element.getHandleIdentifier()));
                }
            } else if (proxy.getType() == PROJECT) {
                IPackageFragmentRoot[] packageRoots = project.getAllPackageFragmentRoots();
                for (IPackageFragmentRoot packageRoot : packageRoots) {
                    if (!packageRoot.isArchive()) {
                        builder.addAll(getPackagesFromRoot(project, packageRoot.getHandleIdentifier()));
                    }
                }
            }
            return false;
        }

        private Set<String> getPackagesFromRoot(IJavaProject project, String handleId) throws CoreException {
            Builder<String> setBuilder = ImmutableSet.builder();
            IPackageFragmentRoot[] roots = project.getPackageFragmentRoots();
            for (IPackageFragmentRoot root : roots) {
                if (handleId.equals(root.getHandleIdentifier())) {
                    setBuilder.addAll(packagesFrom(root));
                }
            }
            return setBuilder.build();
        }

        private Set<String> packagesFrom(IPackageFragmentRoot root) throws CoreException {
            Builder<String> setBuilder = ImmutableSet.builder();
            IJavaElement[] elements = root.getChildren();
            for (IJavaElement element : elements) {
                if (element instanceof IPackageFragment) {
                    IPackageFragment packge = (IPackageFragment) element;
                    setBuilder.addAll(packagesFrom(packge));
                }

            }
            return setBuilder.build();
        }

        private Set<String> packagesFrom(IPackageFragment packageFragment) throws CoreException {
            Builder<String> setBuilder = ImmutableSet.builder();
            if (packageFragment.getElementName().equals(DEFAULT_PACKAGE_NAME)) {
                setBuilder.addAll(classesFromDefaultPackage(packageFragment));
            } else {
                if (packageFragment.getCompilationUnits().length > 0) {
                    setBuilder.add(packageFragment.getElementName() + ".*");
                }
            }
            return setBuilder.build();
        }

        private Set<String> classesFromDefaultPackage(IPackageFragment packageFragment) throws CoreException {
            Builder<String> setBuilder = ImmutableSet.builder();
            for (ICompilationUnit c : packageFragment.getCompilationUnits()) {
                for (IType type : c.getAllTypes()) {
                    setBuilder.add(type.getFullyQualifiedName());
                }
            }
            return setBuilder.build();
        }
    }

    public List<String> getPackages(LaunchConfigurationWrapper configurationWrapper) throws CoreException {
        final Builder<String> builder = ImmutableSet.builder();
        IResource[] resources = configurationWrapper.getMappedResources();
        IResourceProxyVisitor visitor = new ProjectLevelProxyVisitor(configurationWrapper, builder);
        for (IResource resource : resources) {
            resource.accept(visitor, NONE);
        }
        return copyOf(builder.build());
    }

}
