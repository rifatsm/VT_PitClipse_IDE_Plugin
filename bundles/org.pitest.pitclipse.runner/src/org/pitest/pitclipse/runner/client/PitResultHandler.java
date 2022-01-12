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

package org.pitest.pitclipse.runner.client;

import org.pitest.pitclipse.runner.PitResults;

/**
 * An object able to process results produced by a PIT application.
 */
@FunctionalInterface
public interface PitResultHandler {
    
    /**
     * Processes PIT results.
     * 
     * @param results
     *          The results to process.
     */
    void handle(PitResults results);
    
}
