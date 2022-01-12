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

package org.pitest.pitclipse.core.launch;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.pitest.pitclipse.runner.PitResults;
import org.pitest.pitclipse.runner.client.PitResultHandler;

/**
 * <p>Notifies all contributions to the {@code results} extension point
 * that new results have been produced by PIT.</p>
 * 
 * <p>Contributions are notified in a background job.</p>
 */
public class ExtensionPointResultHandler implements PitResultHandler {

    public void handle(PitResults results) {
        Job.create("Reporting Pit results", monitor -> {
            new UpdateExtensions(results).run();
            return new Status(IStatus.OK, "org.pitest.pitclipse.core.launch", "ok");
        }).schedule();
    }

}
