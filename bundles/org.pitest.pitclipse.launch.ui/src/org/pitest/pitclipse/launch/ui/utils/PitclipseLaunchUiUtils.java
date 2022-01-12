/*******************************************************************************
 * Copyright 2021 Lorenzo Bettini and contributors
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
package org.pitest.pitclipse.launch.ui.utils;

import org.eclipse.core.runtime.CoreException;
import org.pitest.pitclipse.launch.ui.PitMutatorsTab;

/**
 * A few utilities for the UI.
 * 
 * @author Lorenzo Bettini
 *
 */
public class PitclipseLaunchUiUtils {

    private PitclipseLaunchUiUtils() {
        // Only static utility methods
    }

    @FunctionalInterface
    public static interface RunnableWithCoreException {
        void run() throws CoreException;
    }

    /**
     * Executes the passed lambda and in case of {@link CoreException} sets the
     * error message.
     * 
     * @param runnable
     * @param configurationTab
     */
    public static void executeSafely(RunnableWithCoreException runnable, PitMutatorsTab configurationTab) {
        try {
            runnable.run();
        } catch (CoreException ce) {
            configurationTab.setErrorMessage(ce.getStatus().getMessage());
        }
    }
}
